package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.models.Product;
import com.crumbs.models.ProductVM;
import com.crumbs.models.SalesRecord;
import com.crumbs.models.Shipment;
import com.crumbs.services.InventoryService;
import com.crumbs.util.CrumbsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by low on 17/2/17 12:22 AM.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebIntegrationTest
public class ProductRepoTest {
	private static final Logger logger = LoggerFactory.getLogger(ProductRepoTest.class);

	@Autowired
	InventoryService inventoryService;

	@Test
	public void productStorageTest() {
		Product p = new Product();
		p.setName("apple");
		p.setPrice(123);
		Shipment s = new Shipment();
		s.setProduct(p);
		s.setQuantity(1);
		s.setExpiry(CrumbsUtil.todayLocalDate().minusDays(2));
		s.setShipDate(CrumbsUtil.todayLocalDate());
		Set<Shipment> l = new HashSet<>();
		l.add(s);
		Shipment s2 = new Shipment();
		s2.setProduct(p);
		s2.setQuantity(2);
		s2.setExpiry(CrumbsUtil.todayLocalDate().plusDays(2));
		s2.setShipDate(CrumbsUtil.todayLocalDate());
		l.add(s2);
		Shipment s3 = new Shipment();
		s3.setProduct(p);
		s3.setQuantity(0);
		s3.setExpiry(CrumbsUtil.todayLocalDate().plusDays(2));
		s3.setShipDate(CrumbsUtil.todayLocalDate());
		l.add(s3);
		Shipment s4 = new Shipment();
		s4.setProduct(p);
		s4.setQuantity(4);
		s4.setExpiry(CrumbsUtil.todayLocalDate().plusDays(1));
		s4.setShipDate(CrumbsUtil.todayLocalDate());
		l.add(s4);
		Shipment s5 = new Shipment();
		s5.setProduct(p);
		s5.setQuantity(5);
		s5.setExpiry(CrumbsUtil.todayLocalDate());
		l.add(s5);
		p.setShipments(l);
		SalesRecord r1 = new SalesRecord();
		r1.setQuantity(123456);
		r1.setDate(CrumbsUtil.todayLocalDate().minusDays(4));
		r1.setProduct(p);
		SalesRecord r4 = new SalesRecord();
		r4.setQuantity(12345678);
		r4.setDate(CrumbsUtil.todayLocalDate().minusDays(1));
		r4.setProduct(p);
		SalesRecord r2 = new SalesRecord();
		r2.setQuantity(12345);
		r2.setDate(CrumbsUtil.todayLocalDate().minusDays(5));
		r2.setProduct(p);
		SalesRecord r3 = new SalesRecord();
		r3.setQuantity(1234567);
		r3.setDate(CrumbsUtil.todayLocalDate().minusDays(2));
		r3.setProduct(p);
		Set<SalesRecord> records = new HashSet<>();
		records.add(r1);
		records.add(r2);
		records.add(r3);
		records.add(r4);
		p.setSalesRecords(records);

		inventoryService.storeProduct(p);
		ProductVM outcome = inventoryService.getProduct("apple");
		logger.info(JSON.toJSONString(outcome));
		logger.info(JSON.toJSONString(inventoryService.productToDateQuantityArray(p)));
		logger.info(JSON.toJSONString(inventoryService.productToShipmentQuantityArray(p)));
		inventoryService.deleteProduct("apple");
	}

}
