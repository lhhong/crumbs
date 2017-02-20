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
		List<Integer> aggregatedStock = aggregatedStock(demand, product);
		ProductVM productVM = inventoryService.getProduct(product);
		Prediction prediction = new Prediction();
		prediction.setProduct(productVM);
		for (int i = 0; i < 7; i++) {
			RemainingStock stock = new RemainingStock();
			stock.setQuantity(aggregatedStock.get(i));
			double urgency = calculateUrgency(demand.get(i), aggregatedStock.get(i));
			stock.setUrgency(urgency);
			stock.setUrgencyLevel(getUrgencyLevel(urgency));
			stock.setPercentExtra((int) (100*calcPercentExtra(demand.get(i), aggregatedStock.get(i))));
			prediction.addToStockList(stock);
		}
		return prediction;
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

	private String getUrgencyLevel(double urgency) {
		List<Double> threshold = calcThreshold();
		if (urgency > threshold.get(0)) {
			return "red";
		}
		if (urgency > threshold.get(1)) {
			return "orange";
		}
		if (urgency > threshold.get(2)) {
			return "yellow";
		}
		else {
			return "green";
		}
	}

	private double calcPercentExtra(int demand, int predictedStock) {
		return  ((double) predictedStock) / ((double) demand);
	}

	private double calculateUrgency(int demand, int predictedStock) {
		return urgencyFunction(calcPercentExtra(demand, predictedStock));
	}

	private final double perfectPercentage = 0.15;
	private final double multiplicator = 4;

	private double urgencyFunction(double x) {
		x = (x - perfectPercentage) * multiplicator;
		return ((x* x) * (1- sigmoid(x, 4))) + ((x*x/6) * sigmoid(x, 6));
	}

	private double sigmoid(double x, int alpha) {
		return (1d / (1 + Math.exp(-alpha * x)));
	}

	private List<Double> calcThreshold() {
		List<Double> threshold = new ArrayList<>();
		double severeUrgency = urgencyFunction(0d);
		threshold.add(severeUrgency);
		threshold.add(severeUrgency*2/3);
		threshold.add(severeUrgency/3);
		return threshold;
	}
}
