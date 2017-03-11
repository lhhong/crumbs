package com.crumbs.rest;


import com.crumbs.components.AccountBean;
import com.crumbs.components.EthereumBean;
import com.crumbs.entities.CrumbsContract;
import com.crumbs.repositories.CrumbsContractRepo;
import com.crumbs.services.ContractService;
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
	private EthereumBean ethereumBean;

	@Autowired
	private AccountBean accountBean;

	@Autowired
	private ContractService contractService;

	@Autowired
	private CrumbsContractRepo contractRepo;

	@RequestMapping(value = "/send-contract", method = GET)
	@ResponseBody
	public void sendSampleContract() throws IOException {
		contractService.sendContract();
	}

	@RequestMapping(value = "/add-contract", method = POST)
	@ResponseBody
	public void addContract(@RequestBody String addr) throws IOException {
		contractService.saveContractAddr(addr);
	}

	@RequestMapping(value = "/topup-contract", method = GET)
	@ResponseBody
	public void topUpContract() throws IOException {
		contractService.topUpContract();
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
