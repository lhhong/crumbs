package com.crumbs.ethereum;

import org.apache.log4j.spi.LoggerFactory;
import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.mine.MinerListener;
import org.ethereum.util.BIUtil;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.util.List;

public class EthereumListener extends EthereumListenerAdapter implements MinerListener {

	Logger logger = org.slf4j.LoggerFactory.getLogger(EthereumListener.class);
    Ethereum ethereum;
    private boolean syncDone = false;

    public EthereumListener(Ethereum ethereum) {
        this.ethereum = ethereum;
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

    @Override
    public void miningStarted() {
        logger.info("Miner started");
    }

    @Override
    public void miningStopped() {
        logger.info("Miner stopped");
    }

    @Override
    public void blockMiningStarted(Block block) {
        logger.info("Start mining block: " + block.getShortDescr());
    }

    @Override
    public void blockMined(Block block) {
        logger.info("Block mined! : \n" + block);
    }

    @Override
    public void blockMiningCanceled(Block block) {
        logger.info("Cancel mining block: " + block.getShortDescr());
    }
}
