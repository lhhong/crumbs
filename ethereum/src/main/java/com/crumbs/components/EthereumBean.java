package com.crumbs.components;

import com.alibaba.fastjson.JSON;
import com.crumbs.repositories.CrumbsContractRepo;
import com.crumbs.services.StateUpdater;
import com.crumbs.util.BooleanObj;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.TxCancelledException;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListener;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Arrays;
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

	public void addListener(OnBlockListener listener) {
		ethereum.addListener(listener);
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

	//private key of a default super rich guy set in block chain genesis, used to distribute ether to accounts that needed it
	private final String RICH_KEY = "9afb9a8e71fa44275fca9d421760cd712abb1493c396d4d36fd3f0a01f1cc9f6";

	//Whole chunk of "sendTransaction" to send raw data or a packaged transaction object to the block chain
	public void sendTransaction(byte[] data, SendingTxListener listener) {
		sendTransaction(createTx(data), listener);
	}

	public void sendTransaction(Transaction tx, SendingTxListener listener) {
		Future<Transaction> ft = ethereum.submitTransaction(tx);
		Thread t = new Thread(new WaitingThread(tx, ft, listener, ethereum));
		t.start();
	}

	public void sendTransaction(byte[] senderPrivKey, byte[] receiverAddress, long payment, byte[] data) throws TxCancelledException {
		sendTransaction(createTx(senderPrivKey,receiverAddress,payment,data));
	}
	public void sendTransaction(byte[] receiverAddress, byte[] data) throws TxCancelledException {
		sendTransaction(createTx(receiverAddress, data));
	}

	public void sendTransaction(byte[] contractAddr, long payment, byte[] functionCallBytes) throws TxCancelledException {
		sendTransaction(createTx(contractAddr, payment, functionCallBytes));
	}

	public void addTxUpdateListener(TxUpdateListener listener) {
		ethereum.addListener(listener);
	}

	public void sendTransaction(Transaction tx) throws TxCancelledException {
		Thread current = Thread.currentThread();
		BooleanObj error = new BooleanObj();
		error.setFalse();
		addTxUpdateListener((transactionReceipt, pendingTransactionState, block) -> {
			if (Arrays.equals(transactionReceipt.getTransaction().getHash(), tx.getHash())) {
				if (pendingTransactionState.compareTo(EthereumListener.PendingTransactionState.DROPPED) == 0 ||
						!(transactionReceipt.getError() == null || transactionReceipt.getError().equals(""))) {
					logger.error("transaction {} dropped", tx.getHash());
					error.setTrue();
					current.interrupt();
				}
				else {
					logger.info("Transaction state of {}: {}", tx.getHash(), pendingTransactionState.name());
					current.interrupt();
				}
			}
		});
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			logger.info("submitting tx");
		}
		ethereum.submitTransaction(tx);
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			logger.info("Listener returned");
			if (error.getValue()) {
				throw new TxCancelledException();
			}
		}
	}

	public void sendEtherFromRich (byte[] receiveAddr) throws TxCancelledException {
		sendTransaction(createTx(RICH_KEY, receiveAddr, 49200L , null));
	}

	//whole chunk of createTx to create a Transaction object based on different raw input type
	//Ether to transact defaults to 0 unless specified
	//Sender defaults to current user unless specified
	public Transaction createTx(byte[] data) {
		return createTx(new byte[0], data);
	}

	public Transaction createTx(byte[] receiverAddr, byte[] data) {
		return createTx(accountBean.getKey(), receiverAddr, 0, data);
	}

	public Transaction createTx(byte[] receiverAddr, long value, byte[] data) {
		return createTx(accountBean.getKey(), receiverAddr, value, data);
	}

	public Transaction createTx(String senderPrivKey, byte[] receiverAddr, long etherToTransact, byte[] data) {
		return createTx(ByteUtil.hexStringToBytes(senderPrivKey), receiverAddr, etherToTransact, data);
	}

	public Transaction createTx(byte[] senderPrivKey, byte[] receiverAddr, long etherToTransact, byte[] data) {
		ECKey senderKey = ECKey.fromPrivate(senderPrivKey);
		return createTx(senderKey, receiverAddr, etherToTransact, data);
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

	//Obsolete codes from this point onwards

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
				ByteUtil.longToBytesNoLeadZeroes(200000000),
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
