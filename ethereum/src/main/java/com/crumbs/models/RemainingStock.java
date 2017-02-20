package com.crumbs.models;

import com.crumbs.util.DateUtil;
import com.crumbs.util.UrgencyUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by low on 19/2/17 11:03 AM.
 */
@Getter
@Setter
public class RemainingStock {

	private int quantity;
	private double urgency;
	private String urgencyLevel;
	private int percentExtra;
	private Date requestDate;

	public RemainingStock() {}

	public RemainingStock(int demand, int stockLeft, int index) {
		quantity = stockLeft;
		urgency = UrgencyUtil.shortageUrg(demand, stockLeft);
		urgencyLevel = UrgencyUtil.getUrgencyLevel(urgency);
		percentExtra = UrgencyUtil.percentageExtra(demand, stockLeft);
		requestDate = DateUtil.daysFromToday(index);
	}

}
