package com.crumbs.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 21/2/17 12:56 AM.
 */
@Getter
@Setter
@NoArgsConstructor
public class ExceShipVM extends BasicShortExceVM {

	private Date expiry;

	public ExceShipVM(ProductVM product, ExcessShipment eShip) {
		super();
		super.setQuantity(eShip.getQuantity());
		super.setUrgency(eShip.getUrgency());
		super.setUrgencyLevel(eShip.getUrgencyLevel());
		super.setPercentExtra(eShip.getPercentExtra());
		expiry = eShip.getExpiry();
		super.setName(product.getName());
		super.setCategory(product.getCategory());
		super.setPrice(product.getPrice());
		super.setQToOffer(eShip.getQToOffer());
	}

}
