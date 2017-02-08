package com.crumbs.rest;


import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import com.crumbs.models.Test;
import com.crumbs.repositories.TestRepo;
import com.crumbs.services.ContractService;
import com.crumbs.services.TestService;
import com.crumbs.util.CrumbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MyRestController {

	@Autowired
	private TestRepo testRepo;

	@Autowired
	private TestService testService;

	@Autowired
	private EthereumBean ethereumBean;

	@Autowired
	private AccountBean accountBean;

	@Autowired
	private ContractService contractService;

	@RequestMapping(value = "/send-contract", method = GET)
	@ResponseBody
	public void sendSampleContract() throws IOException {
		contractService.sendContract();
	}

	@RequestMapping(value = "/getBalance", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public long getThisAccoutBal() throws IOException {
		return CrumbsUtil.weiToEther(ethereumBean.getAccountBal(accountBean.getAddressAsBytes()));
	}

	@RequestMapping(value = "/getBalance", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public long getAccoutBal(@RequestBody String a) throws IOException {
		return CrumbsUtil.weiToEther(ethereumBean.getAccountBal(a));
	}

	@RequestMapping(value = "/account-addr", method = GET, produces = TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getAccountAddr() {
		return accountBean.getAddressAsString();
	}

}
