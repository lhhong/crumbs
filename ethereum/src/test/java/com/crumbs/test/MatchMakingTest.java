package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.entities.Member;
import com.crumbs.entities.TxAccepted;
import com.crumbs.entities.TxSent;
import com.crumbs.models.BasicShortExceVM;
import com.crumbs.models.ExceShipVM;
import com.crumbs.repositories.MemberRepo;
import com.crumbs.services.MatchMakingSrvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 24/2/17 5:19 PM.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchMakingTest {

	private static final Logger logger = LoggerFactory.getLogger(MatchMakingTest.class);

	@Autowired
	MatchMakingSrvc matchMakingSrvc;

	@Autowired
	MemberRepo memberRepo;

	@Before
	public void setUp() {
		Member own = new Member();
		byte[] addr = {0x3e, 0x2f};
		own.setAddr(addr);
		own.setName("Own Name");
		own.setX(12);
		own.setY(1234);
		memberRepo.save(own);
	}

	@Test
	public void testMatchMake() {

		Member mem1 = new Member(new byte[0], "asd", 123, 1234, false);
		Member mem2 = new Member(new byte[0], "asd2", 13, -14, false);
		//TODO add more members

		TxAccepted tx1 = new TxAccepted();
		tx1.setSender(mem1);
		tx1.setQuantity(123);
		tx1.setPrice(123452341);

		TxAccepted tx2 = new TxAccepted();
		//TODO fill in all info and create more tx

		List<TxAccepted> list = new ArrayList<>();
		list.add(tx1);
		list.add(tx2);

		//For excess, use RemStockVM for shortages
		ExceShipVM excess = new ExceShipVM();
		excess.setQuantity(12412);
		//TODO mock all these for testing

		logger.info(JSON.toJSONString(matchMakingSrvc.getMatchingTx(excess, list), true));
	}


}
