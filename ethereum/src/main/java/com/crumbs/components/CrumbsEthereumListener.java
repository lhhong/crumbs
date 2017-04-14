package com.crumbs.components;

import com.crumbs.entities.CrumbsContract;
import com.crumbs.repositories.CrumbsContractRepo;
import org.apache.commons.io.IOUtils;
import org.ethereum.core.*;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class CrumbsEthereumListener extends EthereumListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CrumbsEthereumListener.class);
	private Ethereum ethereum;

	private EthereumBean bean;
	private CrumbsContractRepo crumbsContractRepo;

	public CrumbsEthereumListener(Ethereum ethereum, EthereumBean bean) {
		this.ethereum = ethereum;
		this.bean = bean;
	}

	@Override
	public void onTransactionExecuted(TransactionExecutionSummary summary) {
	}

	/**
	 * on transaction updates, checks if a contract is included. if yes adds it to the database.
	 * To facilitate easy synchronization of contract across nodes
	 */
	@Override
	public void onPendingTransactionUpdate(TransactionReceipt txReceipt, PendingTransactionState state, Block block) {
		logger.info("@@@@@@@@@@ onPendingTransactionUpdate invoked @@@@@@@@@");
		logger.info("State: " + state.name());
		logger.info("Tx receipt: " + txReceipt.toString());
		logger.info("TX: " + txReceipt.getTransaction().toString());
		if (state.compareTo(PendingTransactionState.PENDING) == 0) {
			//TODO check if database has the transaction and remove it
		}
		if (state.compareTo(PendingTransactionState.INCLUDED) == 0) {
			Transaction tx = txReceipt.getTransaction();
			if (tx.isContractCreation()) {

				if (crumbsContractRepo == null) {
					crumbsContractRepo = bean.getCrumbsContractRepo();
				}
				CrumbsContract crumbsContract = new CrumbsContract();
				InputStream is = getClass().getClassLoader().getResourceAsStream("crumbs_tx-abi");
				String abi = null;
				try {
					abi = IOUtils.toString(is);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

				crumbsContract.setContractName("crumbs_tx");
				crumbsContract.setAbi(abi);
				crumbsContract.setTxHash(tx.getHash());
				crumbsContract.setContractAddr(tx.getContractAddress());
				crumbsContract.setIncluded(true);
				logger.info("CONTRACT ADDRESS DISCOVERED: " +  ByteUtil.toHexString(tx.getContractAddress()));
				crumbsContractRepo.save(crumbsContract);
			}
			//TODO process and save transaction to db
		}
	}

	@Override
	public void onPendingStateChanged(PendingState pendingState) {
		logger.info("@@@@@@@@@@ onPendingStateChanged invoked @@@@@@@@@");
		/*List<Transaction> txs = pendingState.getPendingTransactions();
		for (Transaction tx: txs) {
			logger.info("Pending transactions: " + tx.toString());
		}*/
	}

	/**
	 * runs supermarket transaction state updater when a new block is received
	 */
	@Override
	public void onBlock(Block block, List<TransactionReceipt> receipts) {
		logger.info("Received block: " + block.getNumber());
		if (crumbsContractRepo == null) {
			crumbsContractRepo = bean.getCrumbsContractRepo();
		}
		if (crumbsContractRepo.exists("crumbs_tx")) {
			CrumbsContract contract = crumbsContractRepo.findOne("crumbs_tx");
			if (contract.isIncluded()) {
				bean.getStateUpdater().update();
			}
		}

		List<Transaction> txs = block.getTransactionsList();

		for (Transaction tx : txs) {
			logger.info("RECEIVED TRANSACTION FROM: " + ByteUtil.toHexString(tx.getSender()));
			logger.info("DATA: " + ByteUtil.toHexString(tx.getData()));
		}
	}



	/**
	 *  Mark the fact that you are touching
	 *  the head of the chain
	 *  Not used in prototype as block chain is small enough to be downloaded almost instantly
	 */
	@Override
	public void onSyncDone(SyncState state) {
		System.out.println(" ** SYNC DONE ** ");
	}

}
