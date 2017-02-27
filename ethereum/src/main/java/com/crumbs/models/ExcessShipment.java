package com.crumbs.models;

import com.crumbs.util.DateUtil;
import com.crumbs.util.UrgencyUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 20/2/17 7:48 PM.
 */
@Getter
@Setter
public class ExcessShipment {

	private int quantity;
	private double urgency;
	private String urgencyLevel;
	private double percentExtra;
	private Date expiry;
	private int qToOffer;

	public ExcessShipment() {}

	public ExcessShipment(int initialDispose, int actualDispose, int index, int qToOffer) {
		quantity = actualDispose;
		urgency = UrgencyUtil.excessUrg(initialDispose, actualDispose);
		urgencyLevel = UrgencyUtil.getUrgencyLevel(urgency);
		percentExtra = UrgencyUtil.percentageExtra(initialDispose, actualDispose);
		expiry = DateUtil.daysFromToday(index + 1);
		this.qToOffer = qToOffer;
	}
}
