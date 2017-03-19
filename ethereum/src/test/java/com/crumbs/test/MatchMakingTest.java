package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.entities.Member;
import com.crumbs.entities.TxAccepted;
import com.crumbs.models.ExceShipVM;
import com.crumbs.repositories.MemberRepo;
import com.crumbs.services.MatchMakingSrvc;
import com.crumbs.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
		own.setName("NTUC Bedok Mall");
		own.setX(100);
		own.setY(80);
		own.setOwn(true);
		memberRepo.save(own);
	}

	@Test
	public void testMatchMake() {

		Member own = memberRepo.findByOwn(true).get(0);

		Member mem1 = new Member(new byte[0], "Giant Tampines Mall", "loc", 13, 14, false);
		Member mem2 = new Member(new byte[0], "Cold Storage Katong V", "loc", 53, 3, false);
		Member mem3 = new Member(new byte[0], "Lee Minimart Katong V", "loc", 23, 88, false);
		Member mem4 = new Member(new byte[0], "Bedok Givers Charity", "loc", 45, 60, false);
		//TODO add more members

		Calendar cal = Calendar.getInstance();

		List<TxAccepted> tx_list = new ArrayList<>();
		int numTransactions = 10;
		List<Long> prices = new ArrayList<>(Arrays.asList(1500L, 50L,400L,600L,250L,420L,10L,1300L,550L,350L));
		List<String> names = new ArrayList<>(Arrays.asList("Parmesan Cheese","Onion","Sardines",
															"Marigold Milk","Carrot","Corn","Carrot",
															"Parmesan Cheese","Onion","Marigold Milk"));
		List<Integer> quantities = new ArrayList<>(Arrays.asList(100,500,430,350,200,300,300,150,500,200));

		for (int i = 0;i<numTransactions;i++){
			TxAccepted tx = new TxAccepted();
			tx.setPrice(prices.get(i));
			tx.setItem(names.get(i));
			tx.setQuantity(quantities.get(i));
			tx_list.add(tx);
		}

		cal.set(2017, Calendar.MARCH, 5);
		tx_list.get(0).setExpiry(cal.getTime());
		tx_list.get(0).setSell(Boolean.TRUE);
		tx_list.get(0).setSender(own);

		cal.set(2017, Calendar.MARCH, 4);
		tx_list.get(1).setExpiry(cal.getTime());
		tx_list.get(1).setSell(Boolean.TRUE);
		tx_list.get(1).setSender(mem1);

		cal.set(2017, Calendar.MARCH, 3);
		tx_list.get(2).setExpiry(cal.getTime());
		tx_list.get(2).setSell(Boolean.TRUE);
		tx_list.get(2).setSender(own);

		cal.set(2017, Calendar.MARCH, 1);
		tx_list.get(3).setTxDate(cal.getTime());
		tx_list.get(3).setSell(Boolean.FALSE);
		tx_list.get(3).setSender(mem4);

		cal.set(2017, Calendar.FEBRUARY, 27);
		tx_list.get(4).setTxDate(cal.getTime());
		tx_list.get(4).setItem("test");
		tx_list.get(4).setSell(Boolean.FALSE);
		tx_list.get(4).setSender(mem3);
		tx_list.get(4).setQuantity(510);

		cal.set(2017, Calendar.MARCH, 5);
		tx_list.get(5).setExpiry(cal.getTime());
		tx_list.get(5).setSell(Boolean.TRUE);
		tx_list.get(5).setSender(mem2);

		cal.set(2017, Calendar.MARCH, 4);
		tx_list.get(6).setExpiry(cal.getTime());
		tx_list.get(6).setSell(Boolean.TRUE);
		tx_list.get(6).setSender(mem1);

		cal.set(2017, Calendar.FEBRUARY, 27);
		tx_list.get(7).setTxDate(cal.getTime());
		tx_list.get(7).setSell(Boolean.FALSE);
		tx_list.get(7).setSender(own);

		cal.set(2017, Calendar.FEBRUARY, 27);
		tx_list.get(8).setTxDate(cal.getTime());
		tx_list.get(8).setSell(Boolean.FALSE);
		tx_list.get(8).setSender(own);

		cal.set(2017, Calendar.MARCH, 5);
		tx_list.get(9).setExpiry(cal.getTime());
		tx_list.get(9).setSell(Boolean.TRUE);
		tx_list.get(9).setSender(mem1);


		//For excess, use RemStockVM for shortages
		ExceShipVM excess = new ExceShipVM();
		excess.setName("test");
		excess.setExpiry(DateUtil.daysFromToday(40));
		excess.setQuantity(500);
		//TODO mock all these for testing

		logger.info(JSON.toJSONString(matchMakingSrvc.getMatchingTx(excess, tx_list), true));
	}


}
