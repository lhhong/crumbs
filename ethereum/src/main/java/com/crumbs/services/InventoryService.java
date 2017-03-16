package com.crumbs.services;

import com.alibaba.fastjson.JSON;
import com.crumbs.entities.Product;
import com.crumbs.entities.SalesRecord;
import com.crumbs.entities.Shipment;
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
 * Deals with shipment datas
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

	public List<StockUpdate> futureStockInArray(String product) {
		Map<LocalDate, StockUpdate> map = futureStock(product);
		logger.info("stock updates: {}", JSON.toJSONString(map));
		List<StockUpdate> list = new ArrayList<>();
		List<LocalDate> dates = new ArrayList<>();
		dates.addAll(map.keySet());
		Collections.sort(dates);
		for (LocalDate date : dates) {
			list.add(map.get(date));
		}
		return list;
	}

	/**
	 * Creates a list of StockUpdate objects given start and end dates
	 * @param product product to get list for
	 * @param start number of days from today to start collating the list (can be negative)
	 * @param end to collate the list until this number of days from today (can be negative)
	 * @return list of StockUpdates
	 */
	public List<StockUpdate> stockUpdateList(String product, int start, int end) {
		Map<LocalDate, StockUpdate> map = stockUpdateMap(product, start, end);
		logger.info("stock updates: {}", JSON.toJSONString(map));
		List<StockUpdate> list = new ArrayList<>();
		List<LocalDate> dates = new ArrayList<>();
		dates.addAll(map.keySet());
		Collections.sort(dates);
		for (LocalDate date : dates) {
			list.add(map.get(date));
		}
		return list;
	}

	/**
	 * Creates a Map of Date and StockUpdate object calculated from shipments
	 * StockUpdate condense shipment data into a daily sequence
	 * @see com.crumbs.models.StockUpdate
	 * @param product name of product to get stock update for
	 * @param begin days from today to start getting the stock update
	 * @param end days from today to get stock for
	 * @return map of StockUpdate objects
	 */
	private Map<LocalDate, StockUpdate> stockUpdateMap(String product, int begin, int end) {

		Map<LocalDate,StockUpdate> result = new HashMap<>();
		for (int i = begin; i <= end; i++) {
			result.put(DateUtil.todayLocalDate().plusDays(i), new StockUpdate());
		}

		Product p = new Product();
		p.setName(product);
		List<Shipment> shipments = shipmentRepo.findByProductAndQuantityNotAndExpiryAfter(p, 0, DateUtil.daysFromToday(begin-1));

		List<ShipmentVM> shipmentVMS = new ArrayList<>();
		shipments.forEach((shipment) -> shipmentVMS.add(new ShipmentVM(shipment)));

		for (ShipmentVM shipment : shipmentVMS) {
			if (shipment.getDateStamp().isBefore(DateUtil.todayLocalDate().plusDays(begin))) {
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
						stockUpdate.addQuantity(shipment.getQuantity());
					}
				}
			}
			else if (shipment.getDateStamp().isBefore(DateUtil.todayLocalDate().plusDays(end+1))) {
				LocalDate date = shipment.getDateStamp();
				result.get(shipment.getDateStamp()).stockUp(shipment.getQuantity());
				date = date.plusDays(1);
				while (date.isBefore(DateUtil.todayLocalDate().plusDays(end+1))) {
					if (date.isEqual(shipment.getExpiry())) {
						result.get(date).dispose(shipment.getQuantity());
						break;
					}
					result.get(date).addQuantity(shipment.getQuantity());
					date = date.plusDays(1);
				}
			}
		}
		return result;
	}

	public Map<LocalDate, StockUpdate> futureStock(String product) {
		return stockUpdateMap(product, 1, 14);
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

	public void deleteAll() {
		productRepo.deleteAll();
		shipmentRepo.deleteAll();
		salesRecordRepo.deleteAll();
	}
}
