package com.crumbs.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by low on 13/3/17 8:55 PM.
 * Predicted demand and inventory data for a product
 */
@AllArgsConstructor
public class ProductSeries {
	List<Integer> startingInventory;
	List<Integer> stock;
	List<Integer> futureDemand;
	List<Integer> endingInventory;
	List<Integer> disposal;
}
