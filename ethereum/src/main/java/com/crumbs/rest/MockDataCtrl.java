package com.crumbs.rest;

import com.alibaba.fastjson.JSON;
import com.crumbs.components.AccountBean;
import com.crumbs.components.EthereumBean;
import com.crumbs.entities.Account;
import com.crumbs.entities.Member;
import com.crumbs.entities.Product;
import com.crumbs.repositories.AccountRepo;
import com.crumbs.services.ContractService;
import com.crumbs.services.InventoryService;
import com.crumbs.services.TransactionService;
import com.crumbs.services.WebSocketSrvc;
import com.crumbs.util.DateUtil;
import org.ethereum.crypto.ECKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by low on 4/2/17 2:58 PM.
 */
@RestController
public class MockDataCtrl {

	@Autowired
	private TransactionService txService;

	@Autowired
	private EthereumBean ethereumBean;

	@Autowired
	private AccountBean accountBean;

	@Autowired
	private ContractService contractService;

	@Autowired
	private WebSocketSrvc webSocketSrvc;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private AccountRepo accountRepo;

	static Random r = new Random();

	private static final Logger logger = LoggerFactory.getLogger(MockDataCtrl.class);

	@RequestMapping(value = "mock_register_id_NOT_1/{id}", method = POST)
	@ResponseBody
	public void mockRegister(@PathVariable("id") int id, @RequestBody Member member) {
		ECKey key = new ECKey();
		Account account = new Account();
		if (id == 1) {
			logger.error("SCREW YOU!!! DON'T POST ACCOUNT WITH ID 1!!!");
			return;
		}
		account.setId(id);
		account.setPrivateKey(key.getPrivKeyBytes());
		accountRepo.save(account);

		member.setAddr(key.getAddress());

	}

	@RequestMapping(value = "/delete_products", method = GET)
	@ResponseBody
	public void deleteAll() {
		inventoryService.deleteAll();
	}

	@RequestMapping(value = "/import", method = POST)
	@ResponseBody
	public void receive(@RequestBody Product p) {
		logger.info(JSON.toJSONString(p, true));
		p.getSalesRecord().forEach(r -> {
			r.setProduct(p);
			r.setDateStamp(DateUtil.toDate(r.getDateStamp()));
		});
		p.getShipmentsRecord().forEach(r -> {
			r.setProduct(p);
			r.setExpiry(DateUtil.toDate(r.getExpiry()));
			r.setDateStamp(DateUtil.toDate(r.getDateStamp()));
		});
		logger.info(JSON.toJSONString(p, true));
		inventoryService.storeProduct(p);
	}

	@RequestMapping(value = "/sample-contract", method = GET)
	@ResponseBody
	public void sendSampleContract() throws IOException {
		contractService.testContract();
	}

	@RequestMapping(value = "/modify-sample-contract", method = GET)
	@ResponseBody
	public void modifySampleContract() throws IOException {
		contractService.modifyMortalGreeting();
	}

	@RequestMapping(value = "/test-sample-contract", method = GET)
	@ResponseBody
	public void testSampleContract() throws IOException {
		contractService.callMortalGreet();
	}

	@RequestMapping(value = "/bestBlock", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getBestBlock() throws IOException {
		return ethereumBean.getBestBlock();
	}

	@RequestMapping(value = "/adminInfo", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAdminInfo() throws IOException {
		return ethereumBean.getAdminInfo();
	}

	@RequestMapping(value = "/sendMockTx", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public void sendMockTx(@RequestParam ("sender") String sender, @RequestParam("receiver") String receiver) throws IOException {
		ethereumBean.sendMockTx(sender, receiver);
	}
}
