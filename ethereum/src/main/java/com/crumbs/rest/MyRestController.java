package com.crumbs.rest;


import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import com.crumbs.models.Test;
import com.crumbs.repositories.TestRepo;
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

    @RequestMapping(value = "/getBalance", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getThisAccoutBal() throws IOException {
        return ethereumBean.getAccountBal(accountBean.getAddressAsBytes()).toString();
    }

    @RequestMapping(value = "/getBalance", method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAccoutBal(@RequestBody String a) throws IOException {
        return ethereumBean.getAccountBal(a).toString();
    }

    @RequestMapping(value = "/sendMockTx", method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public void sendMockTx(@RequestParam ("sender") String sender, @RequestParam("receiver") String receiver) throws IOException {
        ethereumBean.sendMockTx(sender, receiver);
    }

    @RequestMapping(value = "/test", method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveRandom(@RequestParam ("id") long id, @RequestParam("msg") String msg) {
        Test t = new Test();
        t.setId(id);
        t.setMyString(msg);
        testRepo.save(t);
    }

    @RequestMapping(value = "/test/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Test getRandom(@PathVariable ("id") long id) {
    	return testService.getById(id);
    }

    @RequestMapping(value = "/account-addr", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAccountAddr() {
        return accountBean.getAddressAsString();
    }
}
