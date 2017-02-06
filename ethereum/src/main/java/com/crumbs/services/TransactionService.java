package com.crumbs.services;

import com.crumbs.models.Member;
import com.crumbs.models.TxAccepted;
import com.crumbs.models.TxSent;
import com.crumbs.repositories.TxSentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
	}

	public void accept(String uuid, BigInteger transportPrice, long payment) {
		contractService.sendToTxContract("accept", payment, uuid, transportPrice);
	}

	public void agree(String uuid, long payment) {
		contractService.sendToTxContract("agree", payment, uuid);
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
			try {
				TxAccepted tx = new TxAccepted();
				Member from = new Member();
				from.setName((String) result[0]);
				from.setX((long) result[1]);
				from.setY((long) result[2]);
				tx.setUuid(key);
				tx.setFrom(from);
				tx.setPrice((BigInteger) result[3]);
				tx.setItem((String) result[4]);
				tx.setQuantity((int) result[5]);
				tx.setExpiry(new Date((long) result[6]));
				tx.setSell((boolean) result[7]);
				txs.add(tx);
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
