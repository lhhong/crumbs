package com.crumbs.components;

import org.ethereum.core.Transaction;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by low on 4/2/17 3:17 PM.
 */
public class WaitingThread implements Runnable {

	private SendingTxListener listener;
	private Future<Transaction> ft;
	private Transaction tx;
	private static final Logger logger = LoggerFactory.getLogger(WaitingThread.class);

	public WaitingThread(Transaction tx, Future<Transaction> ft, SendingTxListener listener) {
		this.listener  = listener;
		this.ft = ft;
	}

	@Override
	public void run() {
		while (true) {
			if (ft.isCancelled()) {
				logger.warn("transaction [" + ByteUtil.toHexString(tx.getHash()) + "] cancelled");
				listener.isCancelled();
				break;
			}
			if (ft.isDone()) {
				logger.info("transaction is done");
				try {
					Transaction tx = ft.get();
					logger.info(tx.toString());
					listener.isDone(tx);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
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
