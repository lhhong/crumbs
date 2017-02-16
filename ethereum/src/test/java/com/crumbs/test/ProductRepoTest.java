package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.models.Product;
import com.crumbs.models.ProductVM;
import com.crumbs.models.Shipment;
import com.crumbs.services.PredictionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by low on 17/2/17 12:22 AM.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebIntegrationTest
public class ProductRepoTest {
	private static final Logger logger = LoggerFactory.getLogger(ProductRepoTest.class);

	@Autowired
	PredictionService predictionService;

	@Test
	public void productStorageTest() {
		Product p = new Product();
		p.setName("apple");
		p.setPrice(123);
		Shipment s = new Shipment();
		s.setProduct(p);
		Set<Shipment> l = new HashSet<>();
		l.add(s);
		p.setShipments(l);
		predictionService.storeProduct(p);
		ProductVM outcome = predictionService.getProduct("apple");
		logger.info(JSON.toJSONString(outcome));
		predictionService.deleteProduct("apple");
	}
}
