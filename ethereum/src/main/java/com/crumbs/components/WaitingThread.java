package com.crumbs.components;

import org.ethereum.core.Transaction;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListener;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by low on 4/2/17 3:17 PM.
 * Waits for a transaction to be sent to block chain and calls method in SendingTxListener
 */
public class WaitingThread implements Runnable {

	private SendingTxListener sendingTxListener;
	private TxUpdateListener txUpdateListener;
	private Future<Transaction> ft;
	private Transaction tx;
	private Thread callingThread;
	private Ethereum ethereum;
	boolean carryOn = true;
	private static final Logger logger = LoggerFactory.getLogger(WaitingThread.class);

	public WaitingThread(Transaction tx, Future<Transaction> ft, SendingTxListener sendingTxListener, Ethereum ethereum) {
		this.sendingTxListener = sendingTxListener;
		this.tx = tx;
		this.callingThread = callingThread;
		this.ethereum = ethereum;
		this.txUpdateListener = ((transactionReceipt, pendingTransactionState, block) -> {
			if (Arrays.equals(transactionReceipt.getTransaction().getHash(), this.tx.getHash())) {
				if (pendingTransactionState.compareTo(EthereumListener.PendingTransactionState.DROPPED) == 0) {
					logger.error("transaction {} dropped", tx.getHash());
					sendingTxListener.isCancelled();
				}
				else {
					logger.info("Transaction ste of {}: {}", tx.getHash(), pendingTransactionState.name());
					sendingTxListener.isDone(tx);
				}
				carryOn = false;
			}
		});
		this.ft = ft;
	}

	@Override
	public void run() {
		ethereum.addListener(txUpdateListener);
		while (carryOn) {
			if (ft.isCancelled()) {
				logger.warn("transaction [" + ByteUtil.toHexString(tx.getHash()) + "] cancelled");
				sendingTxListener.isCancelled();
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
