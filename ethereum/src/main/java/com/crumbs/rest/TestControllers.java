package com.crumbs.rest;

import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import com.crumbs.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by low on 4/2/17 2:58 PM.
 */
@RestController
public class TestControllers {

	@Autowired
	private EthereumBean ethereumBean;

	@Autowired
	private AccountBean accountBean;

	@Autowired
	private ContractService contractService;

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
