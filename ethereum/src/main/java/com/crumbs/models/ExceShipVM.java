package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 21/2/17 12:56 AM.
 */
@Getter
@Setter
public class ExceShipVM {

	private int quantity;
	private double urgency;
	private String urgencyLevel;
	private double percentExtra;
	private Date expiry;
	private String name;
	private String category;
	private long price;

	public ExceShipVM(ProductVM product, ExcessShipment eShip) {
		quantity = eShip.getQuantity();
		urgency = eShip.getUrgency();
		urgencyLevel = eShip.getUrgencyLevel();
		percentExtra = eShip.getPercentExtra();
		expiry = eShip.getExpiry();
		name = product.getName();
		category = product.getCategory();
		price = product.getPrice();
	}

}
