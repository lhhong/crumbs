package com.crumbs.components;

import org.ethereum.core.Transaction;

/**
 * Created by low on 4/2/17 3:15 PM.
 * Future listener when sending transactions
 */
public interface SendingTxListener {
	void isDone(Transaction tx);
	void isCancelled();
}
