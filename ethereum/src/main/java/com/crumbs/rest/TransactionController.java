package com.crumbs.rest;

import com.crumbs.ethereum.CheckIncludedListener;
import com.crumbs.models.*;
import com.crumbs.services.Optimize;
import com.crumbs.services.TransactionService;
import com.crumbs.util.CrumbsUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
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

	@RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public TxStatus getStatus() {
		return transactionService.getTxStatus();
	}

	@RequestMapping(value = "/register", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void register(@RequestBody Member member) {
		transactionService.register(member.getName(), member.getX(), member.getY());
	}

	@RequestMapping(value = "/offer", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean postOffer(@RequestBody TransactionVM offer) {
		String uuid = transactionService.newOffer(offer);
		ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
		Thread t = Thread.currentThread();
		transactionService.addListener(tx -> {
			logger.info("onIncluded called");
			if (tx.getUuid().equalsIgnoreCase(uuid)) {
				logger.info("sending included receipt to client for offer {}", uuid);
				t.interrupt();
			}
		});
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			return true;
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
	public void accept(@RequestBody TransactionVM tx) {
		transactionService.accept(tx.getUuid(), tx.getTransportPrice());
	}

	@RequestMapping(value = "/agree", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void agree(@RequestBody String uuid) throws IOException {
		transactionService.agree(uuid);
	}
}
