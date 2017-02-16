package com.crumbs.services;

import com.alibaba.fastjson.JSON;
import com.crumbs.models.Product;
import com.crumbs.models.ProductVM;
import com.crumbs.models.Shipment;
import com.crumbs.models.TransactionVM;
import com.crumbs.repositories.ProductRepo;
import com.fasterxml.jackson.core.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by low on 16/2/17 10:34 PM.
 */
@Service
public class PredictionService {

	private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ProductRepo productRepo;

	private Map<Date,Integer> productToDateQuantityMap(Product product) {
		Map<Date, Integer> result = new HashMap<>();
		for (Shipment shipment : product.getShipments()) {
			result.put(shipment.getExpiry(), shipment.getQuantity());
		}
		return result;
	}

	public ProductVM getProduct(String product) {
		return new ProductVM(productRepo.findOne(product));
	}

	public void storeProduct(Product product) {
		productRepo.save(product);
	}

	public void deleteProduct(String product) {
		productRepo.delete(product);
	}

	public void test() {
		restTemplate.postForEntity("url", new Object(), TransactionVM.class);
	}
}
