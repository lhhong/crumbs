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
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

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

	public void sendContract() throws IOException {
		StringBuilder bin = new StringBuilder("");
		ClassLoader loader = getClass().getClassLoader();
		File file = new File(loader.getResource("crumbs_tx").getFile());
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			bin.append(scanner.nextLine());
		}
		scanner.close();

		StringBuilder abi = new StringBuilder("");
		file = new File(loader.getResource("crumbs_tx-abi").getFile());
		scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			abi.append(scanner.nextLine());
		}
		scanner.close();

		sendContract(bin.toString(), abi.toString(), "crumbs_tx");
	}

	public void sendContract(String compiledContract, String abi, String name) {

		SendingTxListener listener = new SendingTxListener() {
			@Override
			public void isDone(Transaction tx) {
				CrumbsContract crumbsContract = new CrumbsContract();
				crumbsContract.setContractName(name);
				crumbsContract.setAbi(abi);
				crumbsContract.setBin(compiledContract);
				crumbsContract.setIncluded(false);
				logger.info("CONTRACT ADDRESS ON CREATION: " + ByteUtil.toHexString(tx.getContractAddress()));
				crumbsContractRepo.save(crumbsContract);
			}

			@Override
			public void isCancelled() {
				logger.warn("transaction cancelled");
				//TODO notify of failed tx
			}
		};
		ethereumBean.sendTransaction(Hex.decode(compiledContract), listener);
	}

	public void sendToTxContract(String functionName, long payment, Object... args) {
		CrumbsContract contractDef = crumbsContractRepo.findOne("crumbs_tx");
		if (contractDef == null) {
			logger.error("crumbs_tx contract not loaded");
			return;
		}
		CallTransaction.Contract contract = new CallTransaction.Contract(contractDef.getAbi());
		byte[] functionCallBytes = contract.getByName(functionName).encode(args);
		ethereumBean.sendTransaction(contractDef.getContractAddr(), payment, functionCallBytes);
		logger.info("transaction to crumbs_tx sent");
	}

	public Object[] constFunction(String functionName, Object... args) {
		CrumbsContract contractDef = crumbsContractRepo.findOne("crumbs_tx");
		if (contractDef == null) {
			logger.error("crumbs_tx contract not loaded");
			return null;
		}
		CallTransaction.Contract contract = new CallTransaction.Contract(contractDef.getAbi());
		ProgramResult r = ethereumBean.callConstantFunction(contractDef.getContractAddr(), contract.getByName(functionName), args);
		if (r.getException() != null) {
			r.getException().printStackTrace();
			return null;
		}
		else {
			return contract.getByName(functionName).decodeResult(r.getHReturn());
		}
	}

	public void topUpContract() {
		sendToTxContract("topup", 100);
	}

	/******************************************************************************/
	/*************** OBSOLETE FUNCTIONS NO LONGER USED ****************************/
	/******************************************************************************/

	public void compileAndSend(String contract, String name) throws IOException {
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
				crumbsContract.setContractName(name);
				crumbsContract.setAbi(metadata.abi);
				crumbsContract.setBin(metadata.bin);
				crumbsContract.setSolc(metadata.solInterface);
				crumbsContract.setMetadata(metadata.metadata);
				crumbsContract.setIncluded(false);
				logger.info("CONTRACT ADDRESS ON CREATION: " + ByteUtil.toHexString(tx.getContractAddress()));
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

	private final String SAMPLE_CONTRACT = "contract mortal { address owner; function mortal() { owner = msg.sender; } function kill() { if (msg.sender == owner) selfdestruct(owner); } } contract greeter is mortal {string greeting = \"default\"; function changeGreeter(string _greeting) public { greeting = _greeting; } function greet() constant returns (string) { return greeting; } }";

	public void testContract() throws IOException{
		compileAndSend(SAMPLE_CONTRACT, "mortal_tx");
	}

	public void modifyMortalGreeting() {

		CrumbsContract testContract = crumbsContractRepo.findOne("mortal_contract");
		logger.info("loaded contract : " + JSON.toJSONString(testContract, true) );
		if (!testContract.isIncluded()) {
			logger.warn("Contract not yet included to chain");
			return;
		}
		logger.info("Calling the contract constructor");
		CallTransaction.Contract contract = new CallTransaction.Contract(testContract.getAbi());
		byte[] functionCallBytes = contract.getByName("changeGreeter").encode("HI THERE!");
		ethereumBean.sendTransaction(testContract.getContractAddr(), functionCallBytes);
		logger.info("Contract modified!");

	}

	public void callMortalGreet() {
		CrumbsContract testContract = crumbsContractRepo.findOne("mortal_contract");
		logger.info("loaded contract : " + JSON.toJSONString(testContract, true) );
		logger.info("Contract address: " + ByteUtil.toHexString(testContract.getContractAddr()));
		if (!testContract.isIncluded()) {
			logger.warn("Contract not yet included to chain");
			return;
		}
		CallTransaction.Contract contract = new CallTransaction.Contract(testContract.getAbi());
		ProgramResult r = ethereumBean.callConstantFunction(Hex.toHexString(testContract.getContractAddr()), accountBean.getKey(),
				contract.getByName("greet"));
		Object[] ret = contract.getByName("greet").decodeResult(r.getHReturn());
		logger.info("result: " + JSON.toJSONString(ret));
		logger.info("Current contract data member value: " + ret[0]);
	}
}
