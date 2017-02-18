package com.crumbs.services;

import com.crumbs.models.*;
import com.crumbs.repositories.ProductRepo;
import com.crumbs.repositories.SalesRecordRepo;
import com.crumbs.repositories.ShipmentRepo;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.DateUtil;
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
		List<Shipment> shipments = shipmentRepo.findByProductAndQuantityNotAndExpiryAfter(product,0, DateUtil.today());
		for (Shipment record : shipments) {
			if (result.containsKey(DateUtil.toLocalDate(record.getShipDate()))) {
				result.merge(DateUtil.toLocalDate(record.getShipDate()), record.getQuantity(), (current, addition) -> (current + addition));
			}
			else {
				result.put(DateUtil.toLocalDate(record.getShipDate()), record.getQuantity());
			}
		}
		return result;
	}

	public Map<LocalDate, StockUpdate> futureStock(Product product) {
		Map<LocalDate,StockUpdate> result = new HashMap<>();
		for (int i = 1; i <= 7; i++) {
			result.put(DateUtil.todayLocalDate().plusDays(i), new StockUpdate());
		}
		List<Shipment> shipments = shipmentRepo.findByProductAndQuantityNotAndExpiryAfter(product, 0, DateUtil.today());
		List<ShipmentVM> shipmentVMS = new ArrayList<>();
		shipments.forEach((shipment) -> shipmentVMS.add(new ShipmentVM(shipment)));
		for (ShipmentVM shipment : shipmentVMS) {
			if (shipment.getShipDate().isBefore(DateUtil.todayLocalDate().plusDays(1))) {
				if (result.keySet().contains(shipment.getExpiry())) {
					result.get(shipment.getExpiry()).dispose(shipment.getQuantity());
					LocalDate date = shipment.getExpiry().minusDays(1);
					while (date.isAfter(DateUtil.todayLocalDate())) {
						result.get(date).addQuantity(shipment.getQuantity());
						date = date.minusDays(1);
					}
				}
				else {
					for (StockUpdate stockUpdate : result.values()) {
						logger.info("{}", shipment.getQuantity());
						stockUpdate.addQuantity(shipment.getQuantity());
					}
				}
			}
			else if (shipment.getShipDate().isBefore(DateUtil.todayLocalDate().plusDays(8))) {
				LocalDate date = shipment.getShipDate();
				result.get(shipment.getShipDate()).stockUp(shipment.getQuantity());
				date = date.plusDays(1);
				while (date.isBefore(DateUtil.todayLocalDate().plusDays(8))) {
					if (date.isEqual(shipment.getExpiry())) {
						result.get(date).dispose(shipment.getQuantity());
						break;
					}
					logger.info("ADDING: at date: {} dealing with quantity {}", date, shipment.getQuantity());
					result.get(date).addQuantity(shipment.getQuantity());
					date = date.plusDays(1);
				}
			}
		}
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
