package com.crumbs.services;

import com.crumbs.ethereum.AccountBean;
import com.crumbs.models.Member;
import com.crumbs.models.TxAccepted;
import com.crumbs.models.TxSent;
import com.crumbs.repositories.MemberRepo;
import com.crumbs.repositories.TxAcceptedRepo;
import com.crumbs.repositories.TxSentRepo;
import com.crumbs.util.CurrencyUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by low on 6/2/17 12:30 PM.
 */
@Service
public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	@Autowired
	ContractService contractService;

	@Autowired
	TxSentRepo txSentRepo;

	@Autowired
	TxAcceptedRepo txAcceptedRepo;

	@Autowired
	AccountBean accountBean;

	@Autowired
	MemberRepo memberRepo;

	//TODO TxSent checkOfferAccepted, TxAccpeted checkAcceptanceAgreed, checkAgreeIncluded, agree(uuid),

	public void register(String name, long x, long y) {
		Member me = new Member();
		me.setName(name);
		me.setAddr(accountBean.getAddressAsBytes());
		me.setOwn(true);
		me.setX(x);
		me.setY(y);
		memberRepo.save(me);
		contractService.sendToTxContract("register", 0, name, x, y);
	}

	public void newOffer(BigInteger price, String item, int quantity, Date expiry, boolean toSell) {
		TxSent tx = new TxSent();
		String uuid = generateUUID();
		tx.setUuid(uuid);
		tx.setExpiry(expiry);
		tx.setSell(toSell);
		tx.setQuantity(quantity);
		tx.setItem(item);
		tx.setPrice(price);
		txSentRepo.save(tx);
		contractService.sendToTxContract("newOffer", 0, uuid, price, item, quantity, expiry.getTime(), toSell);
		logger.info("Created new offer transaction {}", uuid);
	}

	public void checkOfferIncluded() {
		Iterable<TxSent> unincluded = txSentRepo.findByIncluded(false);
		String[] uuids = getAllTxKeys();
		for (TxSent tx : unincluded) {
			for (String uuid : uuids) {
				if (tx.getUuid().equalsIgnoreCase(uuid)) {
					logger.info("Offer transaction {} included!", uuid);
					tx.setIncluded(true);
					txSentRepo.save(tx);
				}
			}
		}
		List<String> uuidList = Arrays.asList(uuids);
		Iterable<TxSent> included = txSentRepo.findByIncludedAndPending(true, false);
		for (TxSent tx : included) {
			if (!uuidList.contains(tx.getUuid())) {
				logger.warn("Offer transaction with uuid: {} excluded", tx.getUuid());
				tx.setIncluded(false);
				txSentRepo.save(tx);
			}
		}
	}

	public void checkAcceptanceIncluded() {
		List<TxAccepted> unincluded = txAcceptedRepo.findByIncluded(false);
		String[] uuids = getAllTxKeys();
		for (TxAccepted tx : unincluded) {
			for (String uuid : uuids) {
				if (tx.getUuid().equalsIgnoreCase(uuid)) {
					logger.info("Accepting transaction {} included!", uuid);
					tx.setIncluded(true);
					txAcceptedRepo.save(tx);
				}
			}
		}
		List<String> uuidList = Arrays.asList(uuids);
		Iterable<TxAccepted> included = txAcceptedRepo.findByIncludedAndDone(true, false);
		for (TxAccepted tx : included) {
			if (!uuidList.contains(tx.getUuid())) {
				logger.warn("Accepting transaction with uuid: {} excluded", tx.getUuid());
				tx.setIncluded(false);
				txAcceptedRepo.save(tx);
			}
		}
	}

	public String[] getAllTxKeys() {
		String keys = (String) contractService.constFunction("getAllKey")[0];
		return keys.split(";");
	}

	public void accept(String uuid, long transportPrice) {
		Object[] result = contractService.constFunction("getTxById", uuid);
		if (result == null) {
			logger.warn("offer {} no longer exist", uuid);
			return;
		}
		TxAccepted tx = new TxAccepted();
		try {
			Member from = memberRepo.findOne((byte[]) result[0]);
			if (from == null) {
				from = new Member();
				from.setAddr((byte[]) result[0]);
				from.setName((String) result[1]);
				from.setX((long) result[2]);
				from.setY((long) result[3]);
			}
			tx.setUuid(uuid);
			tx.setFrom(from);
			tx.setPrice((BigInteger) result[4]);
			tx.setItem((String) result[5]);
			tx.setQuantity((int) result[6]);
			tx.setExpiry(new Date((long) result[7]));
			tx.setSell((boolean) result[8]);
			tx.setPending((boolean) result[9]);
			tx.setDone((boolean) result[10]);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (tx.isPending() || tx.isDone()) {
			logger.warn("offer {} no longer valid", uuid);
			return;
		}
		long payment = 0;
		if (tx.isSell()) {
			payment = CurrencyUtil.weiToEther(tx.getPrice()) + transportPrice;
		}
		accept(tx, transportPrice, payment);
	}

	public void accept(TxAccepted tx, long transportPrice, long payment) {
		contractService.sendToTxContract("accept", payment, tx.getUuid(), CurrencyUtil.etherToWei(transportPrice));
		txAcceptedRepo.save(tx);
		logger.info("Created accepting transaction {}", tx.getUuid());
	}

	public void agree(String uuid, long payment) {
		contractService.sendToTxContract("agree", payment, uuid);
		logger.info("Created agreeing transaction {}", uuid);
	}

	public String[] getAllAvailTxKeys() {
		String keys = (String) contractService.constFunction("getAllAvailKey")[0];
		return keys.split(";");
	}

	public List<TxAccepted> getAllAvailTx() {
		List<TxAccepted> txs = new ArrayList<>();
		String[] keys = getAllAvailTxKeys();
		for (String key : keys) {
			Object[] result = contractService.constFunction("getTxById", key);
			TxAccepted tx = new TxAccepted();
			try {
				Member from = memberRepo.findOne((byte[]) result[0]);
				if (from == null) {
					from = new Member();
					from.setAddr((byte[]) result[0]);
					from.setName((String) result[1]);
					from.setX((long) result[2]);
					from.setY((long) result[3]);
				}
				tx.setUuid(key);
				tx.setFrom(from);
				tx.setPrice((BigInteger) result[4]);
				tx.setItem((String) result[5]);
				tx.setQuantity((int) result[6]);
				tx.setExpiry(new Date((long) result[7]));
				tx.setSell((boolean) result[8]);
				tx.setPending((boolean) result[9]);
				tx.setDone((boolean) result[10]);
				if (tx.isPending() || tx.isDone()) {
					logger.warn("transaction {} changed state", tx.getUuid());
				}
				else {
					txs.add(tx);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		return txs;
	}

	private String generateUUID() {
		UUID id = UUID.randomUUID();
		return (Long.toUnsignedString(id.getMostSignificantBits(), 36) + Long.toUnsignedString(id.getLeastSignificantBits(), 36));
	}
}
