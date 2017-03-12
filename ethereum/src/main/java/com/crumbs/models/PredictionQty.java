package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by low on 13/3/17 3:12 AM.
 * View model used in front-end to view number of shortages / excess
 */
@Getter
@Setter
public class PredictionQty {
	private int excess;
	private int shortage;
	private boolean valid;
}
