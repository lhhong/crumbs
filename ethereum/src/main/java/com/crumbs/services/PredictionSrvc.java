package com.crumbs.services;

import com.crumbs.entities.Product;
import com.crumbs.entities.SalesRecord;
import com.crumbs.models.*;
import com.crumbs.repositories.SalesRecordRepo;
import com.crumbs.util.DateUtil;
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

	@Autowired
	SalesRecordRepo salesRecordRepo;

	public PredictionVM getAndRankPredictions() {
		PredictionVM predictionVM = new PredictionVM();
		List<Prediction> predictions = getAllPredictions();
		//TODO rank predictions and create new ExceShipVM or RemStockVM to be added to predictions
		return predictionVM;
	}

	public List<Prediction> getAllPredictions() {
		restTemplate.postForEntity("url", Integer[].class, Integer[].class);
		List<Prediction> predictions = new ArrayList<>();
		//TODO for each product, send array with buildArrayQuery to python and call buildPredictionVM for the product demand
		return predictions;
	}

	public List<Integer> buildArrayQuery(String product) {
		Product p = new Product();
		p.setName(product);
		List<SalesRecord> salesRecords = salesRecordRepo.findByProductOrderByDateBeforeByDateAsc(p, DateUtil.today());
		List<Integer> query = new ArrayList<>();
		salesRecords.forEach((record) -> query.add(record.getQuantity()));
		return query;
	}

	public Prediction buildPrediction(List<Integer> demand, String product) {
		if (demand.size() != 8) {
			logger.error("demand array not of size 8");
			return null;
		}
		ProductVM productVM = inventoryService.getProduct(product);
		List<StockUpdate> currentStock = inventoryService.futureStockInArray(product);

		Prediction prediction = new Prediction();
		prediction.setProduct(productVM);

		List<Integer> disposals = new ArrayList<>();
		currentStock.forEach(stock -> disposals.add(stock.getDisposed()));

		//NB: currentStocks and disposal start from day 1 (index 0) containing 7 values while demand starts from day 0 containing 8 values
		int carryOver = currentStock.get(0).getCurrentQuantity() - currentStock.get(0).getStock() + currentStock.get(0).getDisposed();
		for (int i = 0; i < 7; i++) {
			int toDeduct = demand.get(i);
			for (int j = i; j < 7; j++) {
				int initialDispose = disposals.get(j);
				disposals.set((j), Integer.max(0, initialDispose - toDeduct));
				toDeduct = toDeduct - initialDispose;
				if (toDeduct < 0) break;
			}

			int predictedStock = carryOver - demand.get(i);
			prediction.addToStockList(new RemainingStock(demand.get(i), predictedStock, i));
			prediction.addToShipmentList(new ExcessShipment(currentStock.get(i).getDisposed(), disposals.get(i), i));
		}
		int predictedStock = carryOver - demand.get(7);
		prediction.addToStockList(new RemainingStock(demand.get(7), predictedStock, 7));
		return prediction;
	}

	/**
	 * @deprecated
	 */
	public List<Integer> aggregatedStock(List<Integer> demand, String product) {
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
