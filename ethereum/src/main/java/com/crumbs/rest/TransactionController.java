package com.crumbs.rest;

import com.crumbs.models.Member;
import com.crumbs.models.TransactionVM;
import com.crumbs.models.TxAccepted;
import com.crumbs.models.TxStatus;
import com.crumbs.services.Optimize;
import com.crumbs.services.TransactionService;
import com.crumbs.util.CrumbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
	public void postOffer(@RequestBody TransactionVM offer) {
		transactionService.newOffer(offer.getPrice(), offer.getItem(), offer.getQuantity(), offer.getExpiry(), offer.isSell());
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
