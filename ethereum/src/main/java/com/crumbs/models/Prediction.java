package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 19/2/17 10:57 AM.
 */
@Getter
@Setter
public class Prediction {

	private ProductVM product;
	private List<RemainingStock> stock = new ArrayList<>();

	public void addToStockList(RemainingStock remainingStock) {
		stock.add(remainingStock);
	}
}
