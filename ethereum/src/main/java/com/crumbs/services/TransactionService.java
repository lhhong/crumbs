package com.crumbs.services;

import com.crumbs.models.TxAccepted;
import com.crumbs.models.TxSent;
import com.crumbs.repositories.TxSentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

	public void newOffer(BigInteger price, String item, int quantity, LocalDateTime expiry, boolean toSell) {
		TxSent tx = new TxSent();
		String uuid = generateUUID();
		tx.setUuid(uuid);
		tx.setExpiry(expiry);
		tx.setSell(toSell);
		tx.setQuantity(quantity);
		tx.setItem(item);
		tx.setPrice(price);
		txSentRepo.save(tx);
		contractService.sendToTxContract("newOffer", 0, uuid, price, item, quantity, expiry, toSell);
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
		List<TxAccepted> txs = new ArrayList();
		return txs;
	}

	private String generateUUID() {
		UUID id = UUID.randomUUID();
		return (Long.toUnsignedString(id.getMostSignificantBits(), 36) + Long.toUnsignedString(id.getLeastSignificantBits(), 36));
	}
}
