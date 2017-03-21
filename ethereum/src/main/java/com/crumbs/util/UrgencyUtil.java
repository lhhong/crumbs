package com.crumbs.util;

/**
 * Created by low on 21/2/17 12:01 AM.
 */
public class UrgencyUtil {

	private static final double PERFECT_PERCENTAGE = 0.2;

	public static double getPerfectExcess() {
		return PERFECT_PERCENTAGE;
	}

	private static double rawPercentage(int initial, int excess) {
		return  ((double) excess) / ((double) initial);
	}

	public static int percentageExtra(int initial, int excess) {
		return ((int) (100 * rawPercentage(initial, excess)));
	}


	public static double shortageUrg(int demand, int predictedStock) {
		return rawPercentage(demand, predictedStock);
	}

	public static double excessUrg(int initialDispose, int actualDispose) {
		return rawPercentage(initialDispose, actualDispose);
	}

	public static String getShortageUrgencyLevel(double urgency) {
		if (urgency < -0.1) {
			return "red";
		}
		else if (urgency < 0) {
			return "orange";
		}
		else if (urgency < 0.15) {
			return "yellow";
		}
		else {
			return "green";
		}
	}
	public static String getExcessUrgencyLevel(double urgency) {
		if (urgency > 0.35) {
			return "red";
		}
		else if (urgency > 0.30) {
			return "orange";
		}
		else if (urgency > 0.25) {
			return "yellow";
		}
		else {
			return "green";
		}
	}
}
