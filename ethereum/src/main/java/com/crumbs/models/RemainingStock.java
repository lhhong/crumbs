package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

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
}
