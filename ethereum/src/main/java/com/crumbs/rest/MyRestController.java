package com.crumbs.rest;


import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import com.crumbs.models.Test;
import com.crumbs.repositories.TestRepo;
import com.crumbs.services.ContractService;
import com.crumbs.services.TestService;
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

	@RequestMapping(value = "/getBalance", method = GET, produces = TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getThisAccoutBal() throws IOException {
		return ethereumBean.getAccountBal(accountBean.getAddressAsBytes()).toString();
	}

	@RequestMapping(value = "/getBalance", method = POST, produces = TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getAccoutBal(@RequestBody String a) throws IOException {
		return ethereumBean.getAccountBal(a).toString();
	}

	@RequestMapping(value = "/account-addr", method = GET, produces = TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getAccountAddr() {
		return accountBean.getAddressAsString();
	}

}
