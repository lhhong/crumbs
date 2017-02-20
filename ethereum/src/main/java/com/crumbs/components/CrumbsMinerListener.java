package com.crumbs.components;

import org.ethereum.core.Block;
import org.ethereum.mine.MinerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by low on 4/2/17 11:00 AM.
 */
public class CrumbsMinerListener implements MinerListener {

	private static final Logger logger = LoggerFactory.getLogger(CrumbsMinerListener.class);

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
		logger.info("Block mined! : " + block.getShortDescr());
	}

	@Override
	public void blockMiningCanceled(Block block) {
		logger.info("Cancel mining block: " + block.getShortDescr());
	}
}

