package com.crumbs.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 13/3/17 8:55 PM.
 * Predicted demand and inventory data for a product
 */
@Getter
public class ProductSeries {
	List<Integer> startingInventory = new ArrayList<>();
	List<Integer> stock = new ArrayList<>();
	List<Integer> demand = new ArrayList<>();
	List<Integer> endingInventory = new ArrayList<>();
	List<Integer> disposal = new ArrayList<>();

	public ProductSeries(List<Integer> startingInventory, List<Integer> stock, List<Integer> demand, List<Integer> endingInventory, List<Integer> disposal) {
		startingInventory.forEach(x -> this.startingInventory.add(x));
		stock.forEach(x -> this.stock.add(x));
		demand.forEach(x -> this.demand.add(x));
		endingInventory.forEach(x -> this.endingInventory.add(x));
		disposal.forEach(x -> this.disposal.add(x));
	}
}
