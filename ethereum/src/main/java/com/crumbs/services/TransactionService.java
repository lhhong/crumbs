package com.crumbs.services;

import com.crumbs.components.AccountBean;
import com.crumbs.components.CheckIncludedListener;
import com.crumbs.entities.*;
import com.crumbs.models.*;
import com.crumbs.repositories.*;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.TxCancelledException;
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

	@Autowired
	ProductRepo productRepo;

	@Autowired
	ShipmentRepo shipmentRepo;

	@Autowired
	PredictionCacheSrvc predictionCache;

	private List<CheckIncludedListener> checkIncludedListeners = new ArrayList<>();

	public void addListener(CheckIncludedListener listener) {
		checkIncludedListeners.add(listener);
	}

	public void dropListener(CheckIncludedListener listener) {
		checkIncludedListeners.remove(listener);
	}

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
		List<TxSent> offersAccepted = txSentRepo.findByIncludedAndPendingAndDone(true, true, false);
		offersAccepted.forEach((tx) -> status.getOffersAccepted().add(CrumbsUtil.toTxVM(tx)));
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

	public List<TransactionVM> getBought(TxStatus status) {
		List<TransactionVM> bought = new ArrayList<>();
		//String name = memberRepo.findByOwn(true).get(0).getName();
		status.getDoneTx().forEach((tx) -> {
			if (tx.isSell() && tx.getAccepter() == null) {
				bought.add(tx);
			} else if (!tx.isSell() && tx.getSender() == null) {
				bought.add(tx);
			}
		});
		return bought;
	}
	public List<TransactionVM> getSold(TxStatus status) {
		List<TransactionVM> sold = new ArrayList<>();
		status.getDoneTx().forEach((tx) -> {
			if (tx.isSell() && tx.getSender() == null) {
				sold.add(tx);
			} else if (!tx.isSell() && tx.getAccepter() == null) {
				sold.add(tx);
			}
		});
		return sold;
	}

	public void register(String name, long x, long y, String location) throws TxCancelledException {
		Member me = new Member();
		me.setName(name);
		me.setAddr(accountBean.getAddressAsBytes());
		me.setOwn(true);
		me.setX(x);
		me.setY(y);
		me.setLocation(location);
		//contractService.sendToTxContract("deleteTx", 0, name);
		contractService.sendToTxContract("register", 0, name, x, y, location);
		memberRepo.save(me);
	}

	public void register(byte[] senderPrivKey, Member mem) throws TxCancelledException {
		contractService.sendToTxContract(senderPrivKey, "register", 0, mem.getName(), mem.getX(), mem.getY(), mem.getLocation());
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
				predictionCache.removeCache(tx.getItem());
				tx.setDone(true);
				txAcceptedRepo.save(tx);
				Shipment shipment = new Shipment();
				shipment.setId((long) tx.getUuid().hashCode());
				shipment.setProduct(productRepo.findOne(tx.getItem()));
				shipment.setExpiry(tx.getExpiry());
				shipment.setDateStamp(tx.getTxDate());
				if (tx.isSell()) {
					shipment.setQuantity(tx.getQuantity());
				}
				else {
					shipment.setQuantity(-tx.getQuantity());
				}
				shipmentRepo.save(shipment);
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
				tx.setDone(false);
				shipmentRepo.delete((long) tx.getUuid().hashCode());
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
					accepter.setX(((BigInteger) result[3]).longValue());
					accepter.setY(((BigInteger) result[4]).longValue());
					accepter.setLocation((String) result[7]);
					memberRepo.save(accepter);
				}
				tx.setAccepter(accepter);
				tx.setTransportPrice(((BigInteger) result[5]).longValue());
				if(tx.isSell()) {
					tx.setTxDate(new Date(((BigInteger) result[6]).longValue()));
				}
				else {
					tx.setExpiry(new Date(((BigInteger) result[6]).longValue()));
				}
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

	public void newOffer(byte[] senderPrivAddr, TxSent tx) throws TxCancelledException {
		long date;
		if (tx.isSell())
			date = tx.getExpiry().getTime();
		else
			date = tx.getTxDate().getTime();
		contractService.sendToTxContract(senderPrivAddr, "newOffer", 0, tx.getUuid(), tx.getPrice(), tx.getItem(),tx.getQuantity(), date, tx.isSell());
	}

	public String newOffer(BasicShortExceVM shortExce) throws TxCancelledException {
		TxSent tx = new TxSent();
		String uuid = generateUUID();
		tx.setUuid(uuid);
		tx.setQuantity(shortExce.getOfferQuantity());
		tx.setItem(shortExce.getName());
		tx.setPrice(shortExce.getPrice());
		long date = 0;
		if (shortExce instanceof ExceShipVM) {
			tx.setSell(true);
			tx.setExpiry(((ExceShipVM) shortExce).getExpiry());
			date = tx.getExpiry().getTime();
		}
		else if (shortExce instanceof RemStockVM) {
			tx.setSell(false);
			tx.setTxDate(((RemStockVM) shortExce).getRequestDate());
			date = tx.getTxDate().getTime();
		}
		else {
			logger.error("Unknown error");
			return null;
		}
		logger.info("new offer: price {}, item {}, quantity {}", tx.getPrice(), tx.getItem(), tx.getQuantity());
		contractService.sendToTxContract("newOffer", 0, uuid, tx.getPrice(), tx.getItem(), tx.getQuantity(), date, tx.isSell());
		txSentRepo.save(tx);
		logger.info("Created new offer transaction {}", uuid);
		return uuid;
	}

	public void checkOfferIncluded() {
		Iterable<TxSent> unincluded = txSentRepo.findByIncludedAndPending(false, false);
		String[] uuids = getAllTxKeys();
		for (TxSent tx : unincluded) {
			for (String uuid : uuids) {
				if (tx.getUuid().equalsIgnoreCase(uuid)) {
					logger.info("Offer transaction {} included!", uuid);
					tx.setIncluded(true);
					txSentRepo.save(tx);
					for (CheckIncludedListener l : checkIncludedListeners) {
						l.onIncluded(tx);
					}
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
						for (CheckIncludedListener l : checkIncludedListeners) {
							l.onIncluded(tx);
						}
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
					Object[] result = contractService.constFunction("checkDoneStatus", uuid);
					if ((boolean) result[0]) {
						logger.info("Agreeing transaction {} included!", uuid);
						predictionCache.removeCache(tx.getItem());
						tx.setIncluded(true);
						txSentRepo.save(tx);
						Shipment shipment = new Shipment();
						shipment.setId((long) tx.getUuid().hashCode());
						shipment.setProduct(productRepo.findOne(tx.getItem()));
						shipment.setExpiry(tx.getExpiry());
						shipment.setDateStamp(tx.getTxDate());
						if (tx.isSell()) {
							shipment.setQuantity(-tx.getQuantity());
						}
						else {
							shipment.setQuantity(tx.getQuantity());
						}
						shipmentRepo.save(shipment);
						for (CheckIncludedListener l : checkIncludedListeners) {
							l.onIncluded(tx);
						}
					}
				}
			}
		}
		List<String> uuidList = Arrays.asList(uuids);
		Iterable<TxSent> included = txSentRepo.findByIncludedAndPendingAndDone(true, true, true);
		for (TxSent tx : included) {
			if (uuidList.contains(tx.getUuid())) {
				Object[] result = contractService.constFunction("checkDoneStatus", tx.getUuid());
				if (!(boolean) result[0]) {
					logger.warn("Agreeing transaction with uuid: {} excluded", tx.getUuid());
					tx.setIncluded(false);
					txSentRepo.save(tx);
					shipmentRepo.delete((long) tx.getUuid().hashCode());
				}
			}
			else {
				logger.error("Agreeing transaction with uuid: {} excluded COMPLETELY", tx.getUuid());
				tx.setIncluded(false);
				txSentRepo.save(tx);
			}
		}
	}

	public Member checkAndGetRegInfo() {
		Member retrieved = getRegInfo();
		if (retrieved == null) {
			return null;
		}
		List<Member> stored = memberRepo.findByOwn(true);
		if (stored.size() != 1) {
			logger.info("No stored own account");
			stored.forEach(s -> memberRepo.delete(s));
		}
		else if (!stored.get(0).equals(retrieved)) {
			logger.info("Invalid account stored, refreshing");
			memberRepo.delete(stored.get(0));
			memberRepo.save(retrieved);
		}
		return retrieved;
	}

	public void deleteTx(String uuid) throws TxCancelledException {
		contractService.sendToTxContract("deleteOffer", 0, uuid);
	}

	//gets registration info from the blockchain
	private Member getRegInfo() {
		Object[] reg = contractService.constFunction("checkRegistration");
		Member mem = null;
		if ((boolean) reg[0]) {
			mem = new Member();
			mem.setName((String) reg[1]);
			mem.setX(((BigInteger) reg[2]).longValue());
			mem.setY(((BigInteger) reg[3]).longValue());
			mem.setLocation((String) reg[4]);
			mem.setAddr((byte[]) reg[5]);
			mem.setOwn(true);
		}
		return mem;
	}

	public String[] getAllTxKeys() {
		String keys = (String) contractService.constFunction("getAllKey")[0];
		return keys.split(";");
	}

	public void accept(String uuid, long transportPrice, Date expiry, Date txDate) throws TxCancelledException {
		accept(new byte[0], uuid, transportPrice, expiry, txDate, false);
	}

	public void accept(byte[] senderPrivKey, String uuid, long transportPrice, Date expiry, Date txDate, boolean isMock) throws TxCancelledException {
		Object[] result = contractService.constFunction("getTxById", uuid);
		if (result == null) {
			logger.warn("offer {} no longer exist", uuid);
			return;
		}
		Member from = null;
		boolean saveMember = false;
		TxAccepted tx = new TxAccepted();
		try {
			from = memberRepo.findOne((byte[]) result[0]);
			if (from == null) {
				from = new Member();
				from.setAddr((byte[]) result[0]);
				from.setName((String) result[1]);
				from.setX(((BigInteger) result[2]).longValue());
				from.setY(((BigInteger) result[3]).longValue());
				from.setLocation((String) result[10]);
				saveMember = true;
			}
			tx.setUuid(uuid);
			tx.setSender(from);
			tx.setPrice(((BigInteger) result[4]).longValue());
			tx.setItem((String) result[5]);
			tx.setQuantity(((BigInteger) result[6]).intValue());
			tx.setExpiry(new Date(((BigInteger) result[7]).longValue()));
			tx.setSell((boolean) result[8]);
			tx.setPending((boolean) result[9]);
			tx.setDone(false);
			tx.setTxDate(new Date(((BigInteger) result[11]).longValue()));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (tx.isPending() || tx.isDone()) {
			logger.warn("offer {} no longer valid", uuid);
			return;
		}
		tx.setPending(true);
		long payment = 0;
		long date = 0;
		if (tx.isSell()) {
			date = txDate.getTime();
			payment = tx.getPrice() + transportPrice;
		}
		else {
			date = expiry.getTime();
		}
		if (isMock) {
			mockAccept(senderPrivKey, tx, transportPrice, payment, date);
		}
		else {
			accept(tx, transportPrice, payment, date);
			if (saveMember) memberRepo.save(from);
		}
	}

	public void accept(TxAccepted tx, long transportPrice, long payment, long date) throws TxCancelledException {
		contractService.sendToTxContract("accept", payment, tx.getUuid(), transportPrice, date);
		txAcceptedRepo.save(tx);
		logger.info("Created accepting transaction {}", tx.getUuid());
	}

	public void mockAccept(byte[] senderPrivKey, TxAccepted tx, long transportPrice, long payment, long date) throws TxCancelledException {
		contractService.sendToTxContract(senderPrivKey,"accept", payment, tx.getUuid(), transportPrice, date);
	}

	public void agree(Account account, String uuid, long payment) throws TxCancelledException {
		contractService.sendToTxContract(account.getPrivateKey(), "agree", payment, uuid);
	}

	public void agree(String uuid) throws TxCancelledException {
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
		agree(uuid, payment);
		txSentRepo.save(tx);
	}

	public void agree(String uuid, long payment) throws TxCancelledException {
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
			if (key.equals("")) continue;
			if (!txSentRepo.exists(key)) {
				Object[] result = contractService.constFunction("getTxById", key);
				TxAccepted tx = new TxAccepted();
				try {
					Member from = memberRepo.findOne((byte[]) result[0]);
					if (from == null) {
						from = new Member();
						from.setAddr((byte[]) result[0]);
						from.setName((String) result[1]);
						from.setX(((BigInteger) result[2]).longValue());
						from.setY(((BigInteger) result[3]).longValue());
						from.setLocation((String) result[10]);
					}
					tx.setUuid(key);
					tx.setSender(from);
					tx.setPrice(((BigInteger) result[4]).longValue());
					tx.setItem((String) result[5]);
					tx.setQuantity(((BigInteger) result[6]).intValue());
					tx.setExpiry(new Date(((BigInteger) result[7]).longValue()));
					tx.setSell((boolean) result[8]);
					tx.setPending((boolean) result[9]);
					tx.setDone(false);
					tx.setTxDate(new Date(((BigInteger) result[11]).longValue()));
					if (tx.isPending() || tx.isDone()) {
						logger.warn("transaction {} changed state", tx.getUuid());
					} else {
						txs.add(tx);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		return txs;
	}

	public String generateUUID() {
		UUID id = UUID.randomUUID();
		return (Long.toUnsignedString(id.getMostSignificantBits(), 36) + Long.toUnsignedString(id.getLeastSignificantBits(), 36));
	}

}
