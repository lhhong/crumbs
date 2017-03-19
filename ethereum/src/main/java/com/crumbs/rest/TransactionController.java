package com.crumbs.rest;

import com.alibaba.fastjson.JSON;
import com.crumbs.components.CheckIncludedListener;
import com.crumbs.components.SendingTxListener;
import com.crumbs.entities.BasicTx;
import com.crumbs.entities.Member;
import com.crumbs.entities.TxAccepted;
import com.crumbs.entities.TxSent;
import com.crumbs.models.*;
import com.crumbs.repositories.TxSentRepo;
import com.crumbs.services.Optimize;
import com.crumbs.services.PredictionCacheSrvc;
import com.crumbs.services.TransactionService;
import com.crumbs.util.TxCancelledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
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

	@Autowired
	private PredictionCacheSrvc predictionCache;

	@Autowired
	private TxSentRepo txSentRepo;

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

	@RequestMapping(value = "/register", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Member> getReg() {
		return ResponseEntity.ok(transactionService.checkAndGetRegInfo());
	}

	@RequestMapping(value = "/register", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Boolean> register(@RequestBody Member member) {
		logger.info(JSON.toJSONString(member));
		try {
			transactionService.register(member.getName(), member.getX(), member.getY(), member.getLocation());
			return ResponseEntity.ok(true);
		} catch (TxCancelledException e) {
			return ResponseEntity.unprocessableEntity().build();
		}
	}

	@RequestMapping(value = "/offer", method = DELETE, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Boolean> deleteOffer(@RequestBody String uuid) {
		try {
			transactionService.deleteTx(uuid);
			txSentRepo.delete(uuid);
			return ResponseEntity.ok(true);
		} catch (TxCancelledException e) {
			return ResponseEntity.unprocessableEntity().build();
		}
	}

	@RequestMapping(value = "/offer_excess", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Boolean> postOfferExcess(@RequestBody ExceShipVM exceShip) {
		try {
			String uuid = transactionService.newOffer(exceShip);
		} catch (TxCancelledException e) {
			logger.error("Transaction not carried out");
			return ResponseEntity.unprocessableEntity().build();
		}
		try {
			predictionCache.hideExcess(exceShip.getName(), exceShip.getExpiry());
		} catch (NullPointerException e) {
			logger.warn("offer not in predictions");
		}
		return ResponseEntity.ok(true);
	}

	@RequestMapping(value = "/offer_shortage", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Boolean> postOfferShort(@RequestBody RemStockVM remStock) {
		try {
			String uuid = transactionService.newOffer(remStock);
		} catch (TxCancelledException e) {
			logger.error("Transaction not carried out");
			return ResponseEntity.unprocessableEntity().build();
		}
		try {
			predictionCache.hideShortage(remStock.getName(), remStock.getRequestDate());
		} catch (NullPointerException e) {
			logger.warn("offer not in predictions");
		}
		return ResponseEntity.ok(true);
	}

	private boolean postOffer(BasicShortExceVM shortExce) throws TxCancelledException {
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
			return true;
		} finally {
			transactionService.dropListener(listener);
		}
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
	public ResponseEntity accept(@RequestBody TransactionVM tx) {
		try {
			transactionService.accept(tx.getUuid(), tx.getTransportPrice(), tx.getExpiry(), tx.getTxDate());
		} catch (TxCancelledException e) {
			logger.error("Transaction not carried out");
			return ResponseEntity.unprocessableEntity().build();
		}
		if (tx.isSell()) {
			predictionCache.hideShortage(tx.getItem(), tx.getTxDate());
		}
		else {
			predictionCache.hideExcess(tx.getItem(), tx.getExpiry());
		}
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/agree", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity agree(@RequestBody String uuid) throws IOException {
		try {
			transactionService.agree(uuid);
		} catch (TxCancelledException e) {
			logger.error("Transaction not carried out");
			return ResponseEntity.unprocessableEntity().build();
		}
		return ResponseEntity.ok().build();
	}
}
