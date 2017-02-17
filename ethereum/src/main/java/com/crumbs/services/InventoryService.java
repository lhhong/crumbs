package com.crumbs.services;

import com.crumbs.models.*;
import com.crumbs.repositories.ProductRepo;
import com.crumbs.repositories.SalesRecordRepo;
import com.crumbs.repositories.ShipmentRepo;
import com.crumbs.util.CrumbsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by low on 16/2/17 10:34 PM.
 */
@Service
public class InventoryService {

	private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ProductRepo productRepo;

	@Autowired
	SalesRecordRepo salesRecordRepo;

	@Autowired
	ShipmentRepo shipmentRepo;

	public Map<LocalDate,Integer> productToShipmentQuantityArray(Product product) {
		Map<LocalDate,Integer> result = new HashMap<>();
		List<Shipment> shipments = shipmentRepo.findByProductAndQuantityNotAndExpiryAfter(product,0, CrumbsUtil.todayLocalDate());
		for (Shipment record : shipments) {
			if (result.containsKey(record.getShipDate())) {
				result.merge(record.getShipDate(), record.getQuantity(), (current, addition) -> (current + addition));
			}
			else {
				result.put(record.getShipDate(), record.getQuantity());
			}
		}
		return result;
	}

	public Map<LocalDate,Integer> buildOneWeekSupply(Product product) {
		Map<LocalDate,Integer> result = new HashMap<>();
		//TODO build this shit
		return result;
	}

	public List<Integer> productToDateQuantityArray(Product product) {
		List<Integer> result = new ArrayList<>();
		List<SalesRecord> records = salesRecordRepo.findByProductOrderByDateAsc(product);
		for (SalesRecord record : records) {
			result.add(record.getQuantity());
		}
		return result;
	}

	public ProductVM getProduct(String product) {
		return CrumbsUtil.toProductVM(productRepo.findOne(product));
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
