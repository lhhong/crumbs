package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 21/2/17 12:49 AM.
 */
@Getter
@Setter
public class RemStockVM {

	private int quantity;
	private double urgency;
	private String urgencyLevel;
	private int percentExtra;
	private Date requestDate;
	private String name;
	private String category;
	private long price;

	public RemStockVM(ProductVM product, RemainingStock rStock) {
		quantity = rStock.getQuantity();
		urgency = rStock.getUrgency();
		urgencyLevel = rStock.getUrgencyLevel();
		percentExtra = rStock.getPercentExtra();
		requestDate = rStock.getRequestDate();
		name = product.getName();
		category = product.getCategory();
		price = product.getPrice();
	}

}
