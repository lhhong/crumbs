package com.crumbs.components;

import com.crumbs.entities.Account;
import com.crumbs.repositories.AccountRepo;
import com.crumbs.util.CrumbsUtil;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

/**
 * Created by low on 3/2/17 10:30 PM.
 */
public class AccountBean {

	private static final Logger logger = LoggerFactory.getLogger(AccountBean.class);
	private ECKey key;

	@Autowired
	AccountRepo accountRepo;

	@Autowired
	EthereumBean ethereumBean;

	public ECKey getKey() {
		if (key == null) {
			if (accountRepo.exists(1)) {
				logger.info("Found existing account");
				key = ECKey.fromPrivate(accountRepo.findOne(1).getPrivateKey());
			} else {
				logger.info("No existing account found. Initializing ...");
				ECKey key = new ECKey();
				this.key = key;
				Account acc = new Account();
				acc.setPrivateKey(key.getPrivKeyBytes());
				accountRepo.save(acc);
			}
			logger.info("Account address: " + getAddressAsString());
		}
		topUp(key);
		return key;
	}

	public void topUp(ECKey key) {
		if (ethereumBean.getAccountBal(key.getAddress()).compareTo(CrumbsUtil.etherToWei(35000)) < 0) {
			logger.info("topping up ether");
			ethereumBean.sendEtherFromRich(key.getAddress());
		}
	}

	public void start() {
		logger.info("AccountBean started");
		logger.info(Boolean.toString(accountRepo == null));
	}

	public byte[] getAddressAsBytes() {
		return getKey().getAddress();
	}

	public String getAddressAsString() {
		return ByteUtil.toHexString(getKey().getAddress());
	}

	public void signTx(Transaction tx) {
		tx.sign(getKey());
	}
}
