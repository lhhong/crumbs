package com.crumbs.ethereum;

import com.crumbs.models.CrumbsContract;
import com.crumbs.repositories.CrumbsContractRepo;
import org.ethereum.core.Block;
import org.ethereum.core.PendingState;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListener;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.util.BIUtil;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class CrumbsEthereumListener extends EthereumListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CrumbsEthereumListener.class);
	private Ethereum ethereum;
	private boolean syncDone = false;

	private EthereumBean bean;
	private CrumbsContractRepo crumbsContractRepo;

	public CrumbsEthereumListener(Ethereum ethereum, EthereumBean bean) {
		this.ethereum = ethereum;
		this.bean = bean;
	}

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
			if (txReceipt.getTransaction().isContractCreation()) {
				if (crumbsContractRepo == null) {
					crumbsContractRepo = bean.getCrumbsContractRepo();
				}
				CrumbsContract crumbsContract = crumbsContractRepo.findOne("mortal_contract");
				crumbsContract.setTxHash(tx.getHash());
				crumbsContract.setContractAddr(tx.getContractAddress());
				crumbsContract.setIncluded(true);
				crumbsContractRepo.save(crumbsContract);
			}
			//TODO process and save transaction to db
		}
	}

	@Override
	public void onPendingStateChanged(PendingState pendingState) {
		List<Transaction> txs = pendingState.getPendingTransactions();
		logger.info("@@@@@@@@@@ onPendingStateChanged invoked @@@@@@@@@");
		for (Transaction tx: txs) {
			logger.info("Pending transactions: " + tx.toString());
		}
	}

	@Override
	public void onBlock(Block block, List<TransactionReceipt> receipts) {
		System.out.println();
		System.out.println("Do something on block: " + block.getNumber());

		List<Transaction> txs = block.getTransactionsList();

		for (Transaction tx : txs) {
			logger.info("RECEIVED TRANSACTION FROM: " + ByteUtil.toHexString(tx.getSender()));
			logger.info("DATA: " + ByteUtil.toHexString(tx.getData()));
		}
		if (syncDone)
			calcNetHashRate(block);

		System.out.println();
	}



	/**
	 *  Mark the fact that you are touching
	 *  the head of the chain
	 */
	@Override
	public void onSyncDone(SyncState state) {

		System.out.println(" ** SYNC DONE ** ");
		syncDone = true;
	}

	/**
	 * Just small method to estimate total power off all miners on the net
	 * @param block
	 */
	private void calcNetHashRate(Block block){

		if ( block.getNumber() > 1000){

			long avgTime = 1;
			long cumTimeDiff = 0;
			Block currBlock = block;
			for (int i=0; i < 1000; ++i){

				Block parent = ethereum.getBlockchain().getBlockByHash(currBlock.getParentHash());
				long diff = currBlock.getTimestamp() - parent.getTimestamp();
				cumTimeDiff += Math.abs(diff);
				currBlock = parent;
			}

			avgTime = cumTimeDiff / 1000;

			BigInteger netHashRate = block.getDifficultyBI().divide(BIUtil.toBI(avgTime));
			double hashRate = netHashRate.divide(new BigInteger("1000000000")).doubleValue();

			System.out.println("Net hash rate: " + hashRate + " GH/s");
		}

	}

}
