package com.crumbs.rest;

import com.alibaba.fastjson.JSON;
import com.crumbs.components.AccountBean;
import com.crumbs.components.EthereumBean;
import com.crumbs.entities.Account;
import com.crumbs.entities.Member;
import com.crumbs.entities.Product;
import com.crumbs.entities.TxSent;
import com.crumbs.models.TransactionVM;
import com.crumbs.repositories.AccountRepo;
import com.crumbs.services.ContractService;
import com.crumbs.services.InventoryService;
import com.crumbs.services.TransactionService;
import com.crumbs.services.WebSocketSrvc;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.DateUtil;
import com.crumbs.util.TxCancelledException;
import org.ethereum.crypto.ECKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
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

	@RequestMapping(value = "test_screwed", method = GET)
	@ResponseBody
	public String testScrewed() {
		logger.info(JSON.toJSONString(contractService.constFunction("getFixed")));
		return (String) contractService.constFunction("getFixed")[0];
	}

	@RequestMapping(value = "test_tx", method = GET)
	@ResponseBody
	public String getTest() {
		logger.info(JSON.toJSONString(contractService.constFunction("getTest")));
		return (String) contractService.constFunction("getTest")[0];
	}

	@RequestMapping(value = "test_tx", method = POST)
	@ResponseBody
	public void setTest(@RequestBody String test) throws TxCancelledException {
		contractService.sendToTxContract("setTest", 0, test);
	}

	@RequestMapping(value = "mock_register/{id}", method = POST)
	@ResponseBody
	public boolean mockRegister(@PathVariable("id") int id, @RequestBody Member member) throws TxCancelledException {
		if (id == 1) {
			logger.error("SCREW YOU!!! DON'T POST ACCOUNT WITH ID 1!!!");
			return false;
		}
		if (accountRepo.exists(id)) {
			logger.error("Account with id {} exists", id);
			return false;
		}
		ECKey key = new ECKey();
		Account account = new Account();
		account.setId(id);
		account.setPrivateKey(key.getPrivKeyBytes());
		accountRepo.save(account);
		ethereumBean.sendEtherFromRich(key.getAddress());

		Thread t = Thread.currentThread();
		ethereumBean.addListener((block) -> {
			if (ethereumBean.getAccountBal(key.getAddress()).compareTo(BigInteger.valueOf(99999999999L)) > 0) {
				t.interrupt();
			}
		});
		try {
			Thread.sleep(80000);
		} catch (InterruptedException e) {
			member.setAddr(key.getAddress());
			txService.register(key.getPrivKeyBytes(), member);
			return true;
		}
		return false;
	}

	@RequestMapping(value = "mock_offer/{memberId}", method = POST)
	@ResponseBody
	public String mockTx(@PathVariable("memberId") int memberId, @RequestBody TxSent txSent) throws TxCancelledException {
		Account account = accountRepo.findOne(memberId);
		String uuid = txService.generateUUID();
		txSent.setUuid(uuid);
		txService.newOffer(account.getPrivateKey(), txSent);
		return uuid;
	}

	@RequestMapping(value = "mock_accept/{memberId}", method = POST)
	@ResponseBody
	public void mockAccept(@PathVariable("memberId") int id, @RequestBody TransactionVM tx) throws TxCancelledException {
		Account account = accountRepo.findOne(id);
		txService.accept(account.getPrivateKey(), tx.getUuid(), tx.getTransportPrice(), tx.getExpiry(), tx.getTxDate(), true);
	}

	@RequestMapping(value = "mock_agree/{memberId}", method = POST)
	public void mockAgree(@PathVariable("memberId") int id, @RequestBody TransactionVM tx) throws TxCancelledException {
		Account account = accountRepo.findOne(id);
		long payment = 0;
		if (!tx.isSell()) {
			payment = tx.getTransportPrice() + tx.getPrice();
		}
		txService.agree(tx.getUuid(), payment);
	}

	@RequestMapping(value = "/delete_products", method = GET)
	@ResponseBody
	public void deleteAll() {
		inventoryService.deleteAll();
	}

	@RequestMapping(value = "/block_chain_members", method = GET)
	@ResponseBody
	public String getAllMembers() {
		return txService.getAllMembers();
	}

	@RequestMapping(value = "/block_chain_tx", method = GET)
	@ResponseBody
	public String[] getAllTx() {
		return txService.getAllTxKeys();
	}

	@RequestMapping(value = "/import", method = POST)
	@ResponseBody
	public void receive(@RequestBody Product p) {
		logger.info("Storing product info of {}", p.getName());
		p.getSalesRecord().forEach(r -> {
			r.setProduct(p);
			r.setDateStamp(DateUtil.toDate(r.getDateStamp()));
		});
		p.getShipmentsRecord().forEach(r -> {
			r.setExpiry(DateUtil.toDate(r.getExpiry()));
			r.setDateStamp(DateUtil.toDate(r.getDateStamp()));
			r.setProduct(p);
		});
		inventoryService.storeProduct(p);
	}

	@RequestMapping(value = "/sample-contract", method = GET)
	@ResponseBody
	public void sendSampleContract() throws IOException {
		contractService.testContract();
	}

	@RequestMapping(value = "/modify-sample-contract", method = GET)
	@ResponseBody
	public void modifySampleContract() throws IOException, TxCancelledException {
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
