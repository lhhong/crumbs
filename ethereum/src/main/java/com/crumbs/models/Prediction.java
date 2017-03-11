package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 19/2/17 10:57 AM.
 * collection of all predictions made
 */
@Getter
@Setter
public class Prediction {

	private ProductVM product;

	//from day 0 to day 7
	private List<RemainingStock> stocks = new ArrayList<>();

	//from day 1 to day 7
	private List<ExcessShipment> shipments = new ArrayList<>();

	public void addToStockList(RemainingStock remainingStock) {
		stocks.add(remainingStock);
	}

	public void addToShipmentList(ExcessShipment excessShipment) {
		shipments.add(excessShipment);
	}
}
