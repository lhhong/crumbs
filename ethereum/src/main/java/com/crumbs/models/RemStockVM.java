package com.crumbs.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 21/2/17 12:49 AM.
 */
@Getter
@Setter
@NoArgsConstructor
public class RemStockVM extends BasicShortExceVM{

	private Date requestDate;

	public RemStockVM(ProductVM product, RemainingStock rStock) {
		super();
		super.setQuantity(rStock.getQuantity());
		super.setUrgency(rStock.getUrgency());
		super.setUrgencyLevel(rStock.getUrgencyLevel());
		super.setPercentExtra(rStock.getPercentExtra());
		requestDate = rStock.getRequestDate();
		super.setName(product.getName());
		super.setCategory(product.getCategory());
		super.setPrice(product.getPrice());
		super.setQToOffer(rStock.getQToOffer());
	}

}
