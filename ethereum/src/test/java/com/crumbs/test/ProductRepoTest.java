package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.entities.Product;
import com.crumbs.models.ProductVM;
import com.crumbs.entities.SalesRecord;
import com.crumbs.entities.Shipment;
import com.crumbs.services.InventoryService;
import com.crumbs.services.PredictionSrvc;
import com.crumbs.util.DateUtil;
import org.junit.After;
import org.junit.Before;
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
public class ProductRepoTest {
	private static final Logger logger = LoggerFactory.getLogger(ProductRepoTest.class);

	@Autowired
	InventoryService inventoryService;

	@Autowired
	PredictionSrvc predictionSrvc;

	@Before
	public void setUpApple() {
		Product p = new Product();
		p.setName("apple");
		p.setPrice(123);
		Shipment s = new Shipment();
		s.setProduct(p);
		s.setQuantity(1);
		s.setExpiry(DateUtil.daysFromToday(-2));
		s.setShipDate(DateUtil.daysFromToday(-8));
		Set<Shipment> l = new HashSet<>();
		l.add(s);
		Shipment s2 = new Shipment();
		s2.setProduct(p);
		s2.setQuantity(2);
		s2.setExpiry(DateUtil.daysFromToday(8));
		s2.setShipDate(DateUtil.daysFromToday(2));
		l.add(s2);
		Shipment s3 = new Shipment();
		s3.setProduct(p);
		s3.setQuantity(3);
		s3.setExpiry(DateUtil.daysFromToday(7));
		s3.setShipDate(DateUtil.daysFromToday(1));
		l.add(s3);
		Shipment s4 = new Shipment();
		s4.setProduct(p);
		s4.setQuantity(4);
		s4.setExpiry(DateUtil.daysFromToday(9));
		s4.setShipDate(DateUtil.today());
		l.add(s4);
		Shipment s5 = new Shipment();
		s5.setProduct(p);
		s5.setQuantity(5);
		s5.setExpiry(DateUtil.daysFromToday(6));
		s5.setShipDate(DateUtil.daysFromToday(-2));
		l.add(s5);
		Shipment s6 = new Shipment();
		s6.setProduct(p);
		s6.setQuantity(6);
		s6.setExpiry(DateUtil.daysFromToday(7));
		s6.setShipDate(DateUtil.daysFromToday(0));
		l.add(s6);
		Shipment s7 = new Shipment();
		s7.setProduct(p);
		s7.setQuantity(46);
		s7.setExpiry(DateUtil.daysFromToday(6));
		s7.setShipDate(DateUtil.daysFromToday(2));
		l.add(s7);
		p.setShipments(l);
		SalesRecord r1 = new SalesRecord();
		r1.setQuantity(123456);
		r1.setDate(new Date(123456));
		r1.setProduct(p);
		SalesRecord r4 = new SalesRecord();
		r4.setQuantity(12345678);
		r4.setDate(new Date(12345678));
		r4.setProduct(p);
		SalesRecord r2 = new SalesRecord();
		r2.setQuantity(12345);
		r2.setDate(new Date(12345));
		r2.setProduct(p);
		SalesRecord r3 = new SalesRecord();
		r3.setQuantity(1234567);
		r3.setDate(new Date(1234567));
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
	}

	@Test
	public void predictionTest() {
		String p ="apple";
		List<Integer> list = new ArrayList<>();
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(7);
		list.add(8);
		list.add(9);
		list.add(10);
		logger.info(JSON.toJSONString(inventoryService.futureStockInArray(p)));
		logger.info(JSON.toJSONString(predictionSrvc.aggregatedStock(list, p)));
	}


	@Test
	public void inventoryMapTest() {
		String p = "apple";
		logger.info(JSON.toJSONString(inventoryService.futureStock(p)));
		logger.info(JSON.toJSONString(inventoryService.futureStockInArray(p)));
	}

	@After
	public void removeApple() {
		inventoryService.deleteProduct("apple");
	}

}
