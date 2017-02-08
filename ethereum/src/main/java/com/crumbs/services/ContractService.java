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

	private final String SAMPLE_CONTRACT = "contract mortal { address owner; function mortal() { owner = msg.sender; } function kill() { if (msg.sender == owner) selfdestruct(owner); } } contract greeter is mortal {string greeting = \"default\"; function changeGreeter(string _greeting) public { greeting = _greeting; } function greet() constant returns (string) { return greeting; } }";

	private final String CRUMBS_CONTRACT = "pragma solidity ^0.4.9; contract transaction { struct TxKey { string uuid; bool deleted; } struct MemKey { address addr; bool deleted; } struct Member { address addr; string name; int64 x; //x, y coordinate of member int64 y; } struct MemberList { mapping(address => Member) members; MemKey[] keys; uint nextKey; } struct Tx { string uuid; Member sender; uint256 price; string item; uint32 quantity; uint64 expiry; bool toSell; Member accepter; uint256 transportPrice; bool pending; bool done; } struct TxList { mapping(string => Tx) txs; TxKey[] keys; uint nextKey; } TxList list; MemberList memList; function register(string _name, int64 _x, int64 _y) { uint key = 0; while (key < memList.nextKey && !memList.keys[key].deleted && !(memList.keys[key].addr == msg.sender)) { key++; } if (key == memList.nextKey) { list.nextKey++; } memList.keys[key].addr = msg.sender; memList.keys[key].deleted = false; memList.members[memList.keys[key].addr].addr = msg.sender; memList.members[memList.keys[key].addr].name = _name; memList.members[memList.keys[key].addr].x = _x; memList.members[memList.keys[key].addr].y = _y; } function newOffer(string _uuid, uint256 _price, string _item, uint32 _quantity, uint64 _expiry, bool _toSell) { if (memList.members[msg.sender].x == 0) { throw; } uint key = 0; while (key < list.nextKey && !list.keys[key].deleted) { key++; } if (key == list.nextKey) { list.nextKey++; } list.keys[key].uuid = _uuid; list.keys[key].deleted = false; list.txs[list.keys[key].uuid].uuid = _uuid; list.txs[list.keys[key].uuid].sender = memList.members[msg.sender]; list.txs[list.keys[key].uuid].price = _price; list.txs[list.keys[key].uuid].item = _item; list.txs[list.keys[key].uuid].quantity = _quantity; list.txs[list.keys[key].uuid].expiry = _expiry; list.txs[list.keys[key].uuid].toSell = _toSell; } function strConcat(string _a, string _b) internal returns (string){ bytes memory _ba = bytes(_a); bytes memory _bb = bytes(_b); string memory ab = new string(_ba.length + _bb.length + 1); bytes memory ba = bytes(ab); byte sc = ';'; uint k = 0; for (uint i = 0; i < _ba.length; i++) ba[k++] = _ba[i]; ba[k++] = sc; for (i = 0; i < _bb.length; i++) ba[k++] = _bb[i]; return string(ba); } function getAllKey() constant returns (string all) { all = \"\"; uint key = 0; while (key < list.nextKey) { if (!list.keys[key].deleted) { all = strConcat(all, list.keys[key].uuid); } } } function getAllAvailKey() constant returns (string all) { all = \"\"; uint key = 0; while (key < list.nextKey) { if (!list.keys[key].deleted) { if (!list.txs[list.keys[key].uuid].pending && !list.txs[list.keys[key].uuid].done) { all = strConcat(all, list.keys[key].uuid); } } } } function getTxById(string _uuid) constant returns (address _addr, string _name, int64 _x, int64 _y, uint256 _price, string _item, uint32 _quantity, uint64 _expiry, bool _toSell, bool _pending, bool _done) { if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) { throw; } _addr = list.txs[_uuid].sender.addr; _name = list.txs[_uuid].sender.name; _x = list.txs[_uuid].sender.x; _y = list.txs[_uuid].sender.y; _price = list.txs[_uuid].price; _item = list.txs[_uuid].item; _quantity = list.txs[_uuid].quantity; _expiry = list.txs[_uuid].expiry; _toSell = list.txs[_uuid].toSell; _pending = list.txs[_uuid].pending; _done = list.txs[_uuid].done; } function checkPendingStatus(string _uuid) constant returns (bool _pending, address _addr, string _name, int64 _x, int64 _y, uint256 _transportPrice) { if (list.txs[_uuid].quantity == 0) { throw; } _pending = list.txs[_uuid].pending; _addr = list.txs[_uuid].accepter.addr; _name = list.txs[_uuid].accepter.name; _x = list.txs[_uuid].accepter.x; _y = list.txs[_uuid].accepter.y; _transportPrice = list.txs[_uuid].transportPrice; } function checkDoneStatus(string _uuid) constant returns (bool _done) { if (list.txs[_uuid].quantity == 0) { throw; } _done = list.txs[_uuid].done; } function accept(string _uuid, uint256 _transportPrice) payable { if (list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) { throw; } if (list.txs[_uuid].toSell) { if (msg.value < (list.txs[_uuid].price + _transportPrice)) { throw; } } Member _accepter = memList.members[msg.sender]; list.txs[_uuid].accepter = _accepter; list.txs[_uuid].pending = true; list.txs[_uuid].transportPrice = _transportPrice; } function stringsEqual(string storage _a, string memory _b) internal returns (bool) { bytes storage a = bytes(_a); bytes memory b = bytes(_b); if (a.length != b.length) return false; for (uint i = 0; i < a.length; i ++) if (a[i] != b[i]) return false; return true; } function agree(string _uuid) payable { if (msg.sender != list.txs[_uuid].sender.addr) { throw; } if (!list.txs[_uuid].pending || list.txs[_uuid].quantity == 0) { throw; } uint256 totalPrice = list.txs[_uuid].price + list.txs[_uuid].transportPrice; if (!list.txs[_uuid].toSell) { if (msg.value < totalPrice) { throw; } if (!list.txs[_uuid].sender.addr.send(totalPrice)) { throw; } } if (!list.txs[_uuid].accepter.addr.send(totalPrice)) { throw; } list.txs[_uuid].done = true; /* uint key = 0; while (key < list.nextKey || !stringsEqual(list.keys[key].uuid, _uuid)) { key++; } list.keys[key].deleted = true; delete list.txs[_uuid]; */ } function deleteTx(string _uuid) { if (msg.sender != list.txs[_uuid].sender.addr) { throw; } uint key = 0; while (key < list.nextKey || !stringsEqual(list.keys[key].uuid, _uuid)) { key++; } list.keys[key].deleted = true; delete list.txs[_uuid]; } }";
	public void testContract() throws IOException{
		compileAndSend(SAMPLE_CONTRACT, "mortal_tx");
	}

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
		//TODO use compiler compileAndSend(result.toString(), "crumbs_tx");
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
		return contract.getByName(functionName).decodeResult(r.getHReturn());
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
