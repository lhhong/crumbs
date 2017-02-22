package com.crumbs.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 21/2/17 1:02 AM.
 */
@Getter
public class PredictionVM {
	List<ExceShipVM> excessShipments = new ArrayList<>();
	List<RemStockVM> stockShortages = new ArrayList<>();

	public void addExcess(ExceShipVM e) {
		excessShipments.add(e);
	}
	public void addShortage(RemStockVM s) {
		stockShortages.add(s);
	}
}
