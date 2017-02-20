package com.crumbs.models;

import com.crumbs.entities.Shipment;
import com.crumbs.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by low on 17/2/17 10:35 PM.
 */
@Getter
@Setter
public class ShipmentVM {

	public ShipmentVM(Shipment shipment) {
		quantity = shipment.getQuantity();
		shipDate = DateUtil.toLocalDate(shipment.getShipDate());
		expiry = DateUtil.toLocalDate(shipment.getExpiry());
	}
	private LocalDate shipDate;
	private LocalDate expiry;
	private int	quantity;
}
