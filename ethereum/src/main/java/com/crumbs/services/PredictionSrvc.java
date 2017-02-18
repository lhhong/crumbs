package com.crumbs.services;

import com.crumbs.models.StockUpdate;
import com.crumbs.models.TransactionVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 18/2/17 4:31 PM.
 */
@Service
public class PredictionSrvc {

	private static final Logger logger = LoggerFactory.getLogger(PredictionSrvc.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	InventoryService inventoryService;

	public void test() {
		restTemplate.postForEntity("url", Integer[].class, Integer[].class);
	}

	public List<Integer> aggregatedStock(List<Integer> demand, String product) {
		if (demand.size() != 8) {
			logger.error("demand array not of size 8");
		}
		List<Integer> predictedStock = new ArrayList<>();
		List<StockUpdate> currentStock = inventoryService.futureStockInArray(product);

		//NB: current stocks start from day 1 (index 0) containing 7 values while demand starts from day 0 containing 8 values
		int carryOver = currentStock.get(0).getCurrentQuantity() - currentStock.get(0).getStock() + currentStock.get(0).getDisposed();
		for (int i = 0; i < 7; i++) {
			predictedStock.add(carryOver - demand.get(i));
			int toDeduct = demand.get(i);
			int j = i;
			while (toDeduct > 0 || j < 7) {
				StockUpdate stock = currentStock.get(j);
				int initialDispose = stock.getDisposed();
				stock.setDisposed(Integer.max(0, initialDispose - toDeduct));
				toDeduct = toDeduct - initialDispose;
				j++;
			}
			//carryOver = Integer.max(0, carryOver - Integer.max(demand.get(i), currentStock.get(i).getDisposed())) + currentStock.get(i).getStock();
			carryOver = Integer.max(0, carryOver - demand.get(i) - currentStock.get(i).getDisposed()) + currentStock.get(i).getStock();
		}
		predictedStock.add(carryOver - demand.get(7));

		return predictedStock;
	}
}
