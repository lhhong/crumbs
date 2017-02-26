package com.crumbs.rest;

import com.crumbs.components.CheckIncludedListener;
import com.crumbs.components.SendingTxListener;
import com.crumbs.entities.BasicTx;
import com.crumbs.entities.Member;
import com.crumbs.entities.TxAccepted;
import com.crumbs.models.*;
import com.crumbs.services.Optimize;
import com.crumbs.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by low on 7/2/17 8:14 PM.
 */
@RestController(value = "/tx")
public class TransactionController {

	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private Optimize optimize;

	@RequestMapping(value = "/all_tx", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public TxStatus getStatus() {
		return transactionService.getTxStatus();
	}

	@RequestMapping(value = "/sold_tx", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TransactionVM> getSold() {
		TxStatus status = transactionService.getTxStatus();
		return transactionService.getSold(status);
	}

	@RequestMapping(value = "/bought_tx", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TransactionVM> getBought() {
		TxStatus status = transactionService.getTxStatus();
		return transactionService.getBought(status);
	}

	@RequestMapping(value = "/register", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void register(@RequestBody Member member) {
		transactionService.register(member.getName(), member.getX(), member.getY());
	}

	@RequestMapping(value = "/offer_excess", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean postOfferExcess(@RequestBody ExceShipVM exceShip) {
		return postOffer(exceShip);
	}

	@RequestMapping(value = "/offer_shortgae", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean postOfferShort(@RequestBody RemStockVM remStock) {
		return postOffer(remStock);
	}

	private boolean postOffer(BasicShortExceVM shortExce) {
		String uuid = transactionService.newOffer(shortExce);
		if (uuid == null) {
			return false;
		}
		Thread t = Thread.currentThread();
		CheckIncludedListener listener = txIncluded -> {
			logger.info("onIncluded called");
			if (txIncluded.getUuid().equalsIgnoreCase(uuid)) {
				logger.info("sending included receipt to client for offer {}", uuid);
				t.interrupt();
			}
		};
		transactionService.addListener(listener);
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			transactionService.dropListener(listener);
			return true;
		}
		transactionService.dropListener(listener);
		return false;
	}

	@RequestMapping(value = "/offer", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TransactionVM> getOffer() {
		List<TxAccepted> list = transactionService.getAllAvailTx();
		return optimize.rankOffers(list);
	}

	@RequestMapping(value = "/accept", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void accept(@RequestBody TransactionVM tx) {
		transactionService.accept(tx.getUuid(), tx.getTransportPrice(), tx.getExpiry(), tx.getTxDate());
	}

	@RequestMapping(value = "/agree", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void agree(@RequestBody String uuid) throws IOException {
		transactionService.agree(uuid);
	}
}
