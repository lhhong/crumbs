package com.crumbs.ethereum;

import com.alibaba.fastjson.JSON;
import com.crumbs.repositories.CrumbsContractRepo;
import com.crumbs.services.StateUpdater;
import com.crumbs.util.CrumbsUtil;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.concurrent.Future;


public class EthereumBean{

	@Autowired
	private AccountBean accountBean;

	@Autowired
	private CrumbsContractRepo crumbsContractRepo;

	@Autowired
	private StateUpdater stateUpdater;

	Ethereum ethereum;
	public final Logger logger = LoggerFactory.getLogger(EthereumBean.class);

	public void start() {
		logger.info("EtehreumBean started");
		this.ethereum = EthereumFactory.createEthereum();
		this.ethereum.addListener(new CrumbsEthereumListener(ethereum, this));
		this.ethereum.getBlockMiner().addListener(new CrumbsMinerListener());
		this.ethereum.getBlockMiner().startMining();
	}

	public CrumbsContractRepo getCrumbsContractRepo() {
		return crumbsContractRepo;
	}

	public StateUpdater getStateUpdater() {
		return stateUpdater;
	}

	public ProgramResult callConstantFunction(byte[] receiveAddress, CallTransaction.Function function, Object... funcArgs) {
		return ethereum.callConstantFunction(Hex.toHexString(receiveAddress), accountBean.getKey(), function, funcArgs);
	}

	public ProgramResult callConstantFunction(String receiveAddress, ECKey senderPrivateKey,
	                                          CallTransaction.Function function, Object... funcArgs) {
		return ethereum.callConstantFunction(receiveAddress, senderPrivateKey, function, funcArgs);
	}

	public final String RICH_KEY = "9afb9a8e71fa44275fca9d421760cd712abb1493c396d4d36fd3f0a01f1cc9f6";
	public final String RICH_ADDR = "c82f55da06ec7a3b1c878aa48ad0f8b78257e6d0";

	public void sendTransaction(byte[] data) {
		Transaction tx = createTx(data);
		sendTransaction(tx);
	}

	public void sendTransaction(byte[] data, SendingTxListener listener) {
		sendTransaction(createTx(data), listener);
	}

	public void sendTransaction(Transaction tx, SendingTxListener listener) {
		Future<Transaction> ft = ethereum.submitTransaction(tx);
		Thread t = new Thread(new WaitingThread(tx, ft, listener));
		t.start();
	}

	public void sendTransaction(byte[] receiverAddress, byte[] data) {
		sendTransaction(createTx(receiverAddress, data));
	}

	public void sendTransaction(byte[] contractAddr, long payment, byte[] functionCallBytes) {
		sendTransaction(createTx(contractAddr, payment, functionCallBytes));
	}

	public void sendTransaction(Transaction tx) {
		SendingTxListener listener = new SendingTxListener() {
			@Override
			public void isDone(Transaction tx) {
				//TODO save tx in database??
			}

			@Override
			public void isCancelled() {
				//TODO send error msg??
			}
		};
		sendTransaction(tx, listener);
	}

	public void sendEtherFromRich (byte[] receiveAddr) {
		sendTransaction(createTx(RICH_KEY, receiveAddr, 9000000000000L , null));
	}

	public Transaction createTx(byte[] data) {
		return createTx(new byte[0], data);
	}

	public Transaction createTx(byte[] receiverAddr, byte[] data) {
		return createTx(accountBean.getKey(), receiverAddr, 0, data);
	}

	public Transaction createTx(byte[] receiverAddr, long value, byte[] data) {
		return createTx(accountBean.getKey(), receiverAddr, value, data);
	}

	public Transaction createTx(String senderPrivKey, String receiverAddr, long etherToTransact, byte[] data) {
		return createTx(ByteUtil.hexStringToBytes(senderPrivKey), receiverAddr, etherToTransact, data);
	}

	public Transaction createTx(String senderPrivKey, byte[] receiverAddr, long etherToTransact, byte[] data) {
		return createTx(ByteUtil.hexStringToBytes(senderPrivKey), receiverAddr, etherToTransact, data);
	}

	public Transaction createTx(byte[] senderPrivKey, String receiverAddr, long etherToTransact, byte[] data) {
		return createTx(senderPrivKey, ByteUtil.hexStringToBytes(receiverAddr), etherToTransact, data);
	}

	public Transaction createTx(ECKey senderKey, String receiverAddr, long etherToTransact, byte[] data) {
		return createTx(senderKey, ByteUtil.hexStringToBytes(receiverAddr), etherToTransact, data);
	}

	public Transaction createTx(byte[] senderPrivKey, byte[] receiverAddr, long etherToTransact, byte[] data) {
		ECKey senderKey = ECKey.fromPrivate(senderPrivKey);
		return createTx(senderKey, receiverAddr, etherToTransact, data);
	}

	public Transaction createContractTx(byte[] contractAddr, long etherToTransact, CallTransaction.Function function, Object... args) {
		ECKey key = accountBean.getKey();
		return CallTransaction.createCallTransaction(
				ethereum.getRepository().getNonce(key.getAddress()).longValue(),
				ethereum.getGasPrice(),
				4000000,
				ByteUtil.toHexString(contractAddr),
				CrumbsUtil.etherToWei(etherToTransact).longValue(),
				function,
				args
		);
	}

	public Transaction createTx(ECKey senderKey, byte[] receiverAddr, long etherToTransact, byte[] data) {
		BigInteger nonce = ethereum.getRepository().getNonce(senderKey.getAddress());
		Transaction tx = new Transaction(
				ByteUtil.bigIntegerToBytes(nonce),
				ByteUtil.longToBytesNoLeadZeroes(ethereum.getGasPrice()),
				ByteUtil.longToBytesNoLeadZeroes(4000000), //gas limit on computation, hard code to high value for prototype purpose
				receiverAddr,
				ByteUtil.bigIntegerToBytes(CrumbsUtil.etherToWei(etherToTransact)),
				data,
				ethereum.getChainIdForNextBlock()
		);
		tx.sign(senderKey);
		return tx;
	}

	public BigInteger getAccountBal(String addr) {
		return ethereum.getRepository().getBalance(ByteUtil.hexStringToBytes(addr));
	}

	public BigInteger getAccountBal(byte[] addr) {
		return ethereum.getRepository().getBalance(addr);
	}

	public String getAdminInfo() {
		return JSON.toJSONString(ethereum.getAdminInfo());
	}

	public void sendMockTx(String sender, String receiver) {
		byte[] senderPrivateKey = ByteUtil.hexStringToBytes(sender);
		byte[] receiveAddress = ByteUtil.hexStringToBytes(receiver);
		byte[] fromAddress = ECKey.fromPrivate(senderPrivateKey).getAddress();
		byte[] data = {(byte) 0x3e};
		BigInteger nonce = ethereum.getRepository().getNonce(fromAddress);
		Transaction tx = new Transaction(
				ByteUtil.bigIntegerToBytes(nonce),
				ByteUtil.longToBytesNoLeadZeroes(ethereum.getGasPrice()),
				ByteUtil.longToBytesNoLeadZeroes(200000),
				receiveAddress,
				ByteUtil.bigIntegerToBytes(BigInteger.valueOf(1)),  // 1_000_000_000 gwei, 1_000_000_000_000L szabo, 1_000_000_000_000_000L finney, 1_000_000_000_000_000_000L ether
				data,
				ethereum.getChainIdForNextBlock());

		tx.sign(ECKey.fromPrivate(senderPrivateKey));
		logger.info("<=== Sending transaction: " + tx);
		ethereum.submitTransaction(tx);
	}

	public String getBestBlock(){
		return "" + ethereum.getBlockchain().getBestBlock().getNumber();
	}

}
