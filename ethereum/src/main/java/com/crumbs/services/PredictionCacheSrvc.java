package com.crumbs.services;

import com.crumbs.models.ExceShipVM;
import com.crumbs.models.Prediction;
import com.crumbs.models.PredictionVM;
import com.crumbs.models.RemStockVM;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by low on 11/3/17 4:51 PM.
 */
@Service
public class PredictionCacheSrvc {

	private List<ExceShipVM> excessCache;
	private List<RemStockVM> shortageCache;

	private List<String> needsCalc = new LinkedList<>();

	private boolean needsFirstRun = true;

	public boolean needsFirstRun() {
		return needsFirstRun;
	}

	public void setPredictionCache(PredictionVM predictionCache) {
		excessCache = predictionCache.getExcessShipments();
		shortageCache = predictionCache.getStockShortages();
		needsFirstRun = false;
	}

	public List<String> getNeedsCalc() {
		return needsCalc;
	}

	public void addPredictions(List<Prediction> predictions) {

		predictions.forEach(p -> {
			p.getShipments().forEach(s -> {
				if (!s.getUrgencyLevel().equalsIgnoreCase("green"))
					excessCache.add(new ExceShipVM(p.getProduct(), s));
			});
			p.getStocks().forEach(s -> {
				if (!s.getUrgencyLevel().equalsIgnoreCase("green"))
					shortageCache.add(new RemStockVM(p.getProduct(), s));
			});
			needsCalc.remove(p.getProduct().getName());
		});
		excessCache.sort((s, s1) -> -Double.compare(s.getUrgency(), s1.getUrgency()));
		shortageCache.sort((s, s1) -> -Double.compare(s.getUrgency(), s1.getUrgency()));

	}

	public void removeCache(String name) {
		excessCache.removeIf(excess -> excess.getName().equals(name));
		shortageCache.removeIf(shortage -> shortage.getName().equals(name));
		needsCalc.add(name);
	}

	public PredictionVM getPredictionCache() {
		PredictionVM predictionVM = new PredictionVM();
		predictionVM.setExcessShipments(excessCache);
		predictionVM.setStockShortages(shortageCache);
		return predictionVM;
	}
}
