package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}
