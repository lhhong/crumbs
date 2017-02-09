package com.crumbs.services;

import com.crumbs.ethereum.AccountBean;
import com.crumbs.models.*;
import com.crumbs.repositories.MemberRepo;
import com.crumbs.repositories.TxAcceptedRepo;
import com.crumbs.repositories.TxSentRepo;
import com.crumbs.util.CrumbsUtil;
import javassist.bytecode.ByteArray;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

	public TxStatus getTxStatus() {
		TxStatus status = new TxStatus();
		List<TxSent> successfulOffer = txSentRepo.findByIncludedAndPendingAndDone(true, false, false);
		for (TxSent tx : successfulOffer) {
			status.getSuccessfulOffers().add(CrumbsUtil.toTxVM(tx));
		}
		List<TxSent> pendingOffer = txSentRepo.findByIncludedAndPendingAndDone(false, false, false);
		for (TxSent tx : pendingOffer) {
			status.getPendingOffers().add(CrumbsUtil.toTxVM(tx));
		}
		List<TxAccepted> successfulAccept = txAcceptedRepo.findByIncludedAndDone(true, false);
		for (TxAccepted tx : successfulAccept) {
			status.getSuccessfulAccepts().add(CrumbsUtil.toTxVM(tx));
		}
		List<TxAccepted> pendingAccept = txAcceptedRepo.findByIncludedAndDone(false, false);
		for (TxAccepted tx : pendingAccept) {
			status.getPendingAccepts().add(CrumbsUtil.toTxVM(tx));
		}
		List<TxSent> pendingAgree = txSentRepo.findByIncludedAndPendingAndDone(false, true, true);
		for (TxSent tx : pendingAgree) {
			status.getPendingAgrees().add(CrumbsUtil.toTxVM(tx));
		}
		List<TxAccepted> acceptedDone = txAcceptedRepo.findByIncludedAndDone(true, true);
		List<TxSent> sentDone = txSentRepo.findByIncludedAndPendingAndDone(true, true, true);
		for (TxAccepted tx : acceptedDone) {
			status.getDoneTx().add(CrumbsUtil.toTxVM(tx));
		}
		for (TxSent tx : sentDone) {
			status.getDoneTx().add(CrumbsUtil.toTxVM(tx));
		}
		return status;
	}

	public void register(String name, long x, long y) {
		Member me = new Member();
		me.setName(name);
		me.setAddr(accountBean.getAddressAsBytes());
		me.setOwn(true);
		me.setX(x);
		me.setY(y);
		memberRepo.save(me);
		//contractService.sendToTxContract("deleteTx", 0, name);
		contractService.sendToTxContract("register", 0, name, BigInteger.valueOf(x), BigInteger.valueOf(y));
	}

	public void checkAcceptanceAgreed() {
		List<TxAccepted> acceptance = txAcceptedRepo.findByIncludedAndDone(true, false);
		for (TxAccepted tx : acceptance) {
			Object[] result = contractService.constFunction("checkDoneStatus", tx.getUuid());
			if (result == null) {
				logger.error("null returned when checking offers");
				continue;
			}
			if ((boolean) result[0]) {
				logger.info("transaction {} agreed", tx.getUuid());
				tx.setDone(true);
				txAcceptedRepo.save(tx);
			}
		}
		List<TxAccepted> done = txAcceptedRepo.findByIncludedAndDone(true, true);
		for (TxAccepted tx : done) {
			Object[] result = contractService.constFunction("checkDoneStatus", tx.getUuid());
			if (result == null) {
				logger.error("null returned when checking offers");
				logger.error("transaction {} excluded when checking agreed", tx.getUuid());
				tx.setDone(false);
				continue;
			}
			if (!(boolean) result[0]) {
				logger.info("transaction {} excluded when checking agreed", tx.getUuid());
				tx.setDone(true);
				txAcceptedRepo.save(tx);
			}
		}
	}

	public void checkOfferAccepted() {
		List<TxSent> offers = txSentRepo.findByIncludedAndPending(true, false);
		for (TxSent tx : offers) {
			Object[] result = contractService.constFunction("checkPendingStatus", tx.getUuid());
			if (result == null) {
				logger.error("null returned when checking offers");
				continue;
			}
			if ((boolean) result[0]) {
				Member accepter = memberRepo.findOne((byte[]) result[1]);
				if (accepter == null) {
					accepter = new Member();
					accepter.setAddr((byte[]) result[1]);
					accepter.setName((String) result[2]);
					accepter.setX((long) result[3]);
					accepter.setY((long) result[4]);
				}
				tx.setAccepter(accepter);
				tx.setTransportPrice((long) result[5]);
				logger.info("transaction {} accepted", tx.getUuid());
				tx.setPending(true);
				txSentRepo.save(tx);
			}
		}
		//check for offers that are rejected
		List<TxSent> accepted = txSentRepo.findByIncludedAndPendingAndDone(true, true, false);
		for (TxSent tx : accepted) {
			Object[] result = contractService.constFunction("checkPendingStatus", tx.getUuid());
			if (result == null) {
				logger.error("null returned when checking offers");
				logger.error("transaction {} excluded when checking", tx.getUuid());
				tx.setPending(false);
				txSentRepo.save(tx);
				continue;
			}
			if (!(boolean) result[0]) {
				logger.info("transaction {} excluded when checking", tx.getUuid());
				tx.setPending(false);
				txSentRepo.save(tx);
			}
		}
	}

	public void newOffer(long price, String item, int quantity, Date expiry, boolean toSell) {
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
		Iterable<TxSent> included = txSentRepo.findByIncluded(true);
		for (TxSent tx : included) {
			if (!uuidList.contains(tx.getUuid())) {
				logger.warn("Offer transaction with uuid: {} excluded", tx.getUuid());
				tx.setIncluded(false);
				txSentRepo.save(tx);
			}
		}
	}

	public void checkAcceptanceIncluded() {
		List<TxAccepted> unincluded = txAcceptedRepo.findByIncludedAndDone(false, false);
		String[] uuids = getAllTxKeys();
		for (TxAccepted tx : unincluded) {
			for (String uuid : uuids) {
				if (tx.getUuid().equalsIgnoreCase(uuid)) {
					Object[] result = contractService.constFunction("checkPendingStatus", uuid);
					if ((boolean) result[0]) {
						logger.info("Accepting transaction {} included!", uuid);
						tx.setIncluded(true);
						txAcceptedRepo.save(tx);
					}
				}
			}
		}
		List<String> uuidList = Arrays.asList(uuids);
		Iterable<TxAccepted> included = txAcceptedRepo.findByIncludedAndDone(true, false);
		for (TxAccepted tx : included) {
			if (uuidList.contains(tx.getUuid())) {
				Object[] result = contractService.constFunction("checkPendingStatus", tx.getUuid());
				if (!(boolean) result[0]) {
					logger.warn("Accepting transaction with uuid: {} excluded", tx.getUuid());
					tx.setIncluded(false);
					txAcceptedRepo.save(tx);
				}
			}
			else {
				logger.error("Accepting transaction with uuid: {} excluded COMPLETELY", tx.getUuid());
				tx.setIncluded(false);
				txAcceptedRepo.save(tx);
			}
		}
	}

	public void checkAgreeIncluded() {
		List<TxSent> unincluded = txSentRepo.findByIncludedAndPendingAndDone(false, true, true);
		String[] uuids = getAllTxKeys();
		for (TxSent tx : unincluded) {
			for (String uuid : uuids) {
				if (tx.getUuid().equalsIgnoreCase(uuid)) {
					Object[] result = contractService.constFunction("checkDone", uuid);
					if ((boolean) result[0]) {
						logger.info("Agreeing transaction {} included!", uuid);
						tx.setIncluded(true);
						txSentRepo.save(tx);
					}
				}
			}
		}
		List<String> uuidList = Arrays.asList(uuids);
		Iterable<TxSent> included = txSentRepo.findByIncludedAndPendingAndDone(true, true, true);
		for (TxSent tx : included) {
			if (uuidList.contains(tx.getUuid())) {
				Object[] result = contractService.constFunction("checkDone", tx.getUuid());
				if (!(boolean) result[0]) {
					logger.warn("Agreeing transaction with uuid: {} excluded", tx.getUuid());
					tx.setIncluded(false);
					txSentRepo.save(tx);
				}
			}
			else {
				logger.error("Agreeing transaction with uuid: {} excluded COMPLETELY", tx.getUuid());
				tx.setIncluded(false);
				txSentRepo.save(tx);
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
				from.setX(((BigInteger) result[2]).longValue());
				from.setY(((BigInteger) result[3]).longValue());
			}
			tx.setUuid(uuid);
			tx.setSender(from);
			tx.setPrice(((BigInteger) result[4]).longValue());
			tx.setItem((String) result[5]);
			tx.setQuantity(((BigInteger) result[6]).intValue());
			tx.setExpiry(new Date(((BigInteger) result[7]).longValue()));
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
		tx.setPending(true);
		long payment = 0;
		if (tx.isSell()) {
			payment = tx.getPrice() + transportPrice;
		}
		accept(tx, transportPrice, payment);
	}

	public void accept(TxAccepted tx, long transportPrice, long payment) {
		contractService.sendToTxContract("accept", payment, tx.getUuid(), transportPrice);
		txAcceptedRepo.save(tx);
		logger.info("Created accepting transaction {}", tx.getUuid());
	}

	public void agree(String uuid) {
		TxSent tx = txSentRepo.findOne(uuid);
		if (tx == null) {
			logger.error("transaction {} to be agreed do not exist", uuid);
			return;
		}
		if (tx.isDone() || !tx.isPending() || !tx.isIncluded()) {
			logger.warn("transaction {} to be agreed not in correct state", uuid);
			return;
		}
		long payment = 0;
		if (!tx.isSell()) {
			payment = tx.getTransportPrice() + tx.getPrice();
		}
		tx.setDone(true);
		tx.setIncluded(false);
		txSentRepo.save(tx);
		agree(uuid, payment);
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
				tx.setSender(from);
				tx.setPrice((long) result[4]);
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