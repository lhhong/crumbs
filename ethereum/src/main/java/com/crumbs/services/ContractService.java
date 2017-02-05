package com.crumbs.services;

import com.alibaba.fastjson.JSON;
import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import com.crumbs.ethereum.SendingTxListener;
import com.crumbs.models.CrumbsContract;
import com.crumbs.repositories.CrumbsContractRepo;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.vm.program.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by low on 4/2/17 2:38 PM.
 */
@Service
public class ContractService {

	private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

	private SolidityCompiler compiler = new SolidityCompiler(null);

	@Autowired
	private EthereumBean ethereumBean;

	@Autowired
	private CrumbsContractRepo crumbsContractRepo;

	@Autowired
	private AccountBean accountBean;

	private final String SAMPLE_CONTRACT = "contract mortal { address owner; function mortal() { owner = msg.sender; } function kill() { if (msg.sender == owner) selfdestruct(owner); } } contract greeter is mortal {string greeting = \"default\"; function greeter(string _greeting) public { greeting = _greeting; } function greet() constant returns (string) { return greeting; } }";

	public void testContract() throws IOException{
		compileAndSend(SAMPLE_CONTRACT);
	}

	public void compileAndSend(String contract) throws IOException {
		logger.info("Compiling contract...");
		SolidityCompiler.Result result = compiler.compileSrc(contract.getBytes(), true, true,
				SolidityCompiler.Options.ABI, SolidityCompiler.Options.BIN);
		if (result.isFailed()) {
			throw new RuntimeException("Contract compilation failed:\n" + result.errors);
		}
		CompilationResult res = CompilationResult.parse(result.output);
		if (res.contracts.isEmpty()) {
			throw new RuntimeException("Compilation failed, no contracts returned:\n" + result.errors);
		}
		CompilationResult.ContractMetadata metadata = res.contracts.values().iterator().next();
		if (metadata.bin == null || metadata.bin.isEmpty()) {
			throw new RuntimeException("Compilation failed, no binary returned:\n" + result.errors);
		}
		logger.info("Compiled metadata: " + JSON.toJSONString(metadata, true));

		SendingTxListener listener = new SendingTxListener() {
			@Override
			public void isDone(Transaction tx) {
				CrumbsContract crumbsContract = new CrumbsContract();
				crumbsContract.setContractName("mortal_contract");
				crumbsContract.setAbi(metadata.abi);
				crumbsContract.setBin(metadata.bin);
				crumbsContract.setSolc(metadata.solInterface);
				crumbsContract.setMetadata(metadata.metadata);
				//crumbsContractRepo.delete("mortal_contract");
				crumbsContractRepo.save(crumbsContract);
			}

			@Override
			public void isCancelled() {
				logger.warn("transaction cancelled");
				//TODO notify of failed tx
			}
		};
		ethereumBean.sendTransaction(Hex.decode(metadata.bin), listener);
	}

	public void getContractResult() {

		CrumbsContract testContract = crumbsContractRepo.findOne("mortal_contract");
		logger.info("loaded contract : " + JSON.toJSONString(testContract, true) );
		logger.info("Calling the contract constructor");
		CallTransaction.Contract contract = new CallTransaction.Contract(testContract.getAbi());
		byte[] functionCallBytes = contract.getConstructor().encodeArguments("HI THERE!");
		ethereumBean.sendTransaction(testContract.getContractAddr(), functionCallBytes);
		logger.info("Contract modified!");

		ProgramResult r = ethereumBean.callConstantFunction(Hex.toHexString(testContract.getContractAddr()), accountBean.getKey(),
				contract.getByName("greet"));
		Object[] ret = contract.getByName("greet").decodeResult(r.getHReturn());
		logger.info("Current contract data member value: " + ret[0]);
	}
}
