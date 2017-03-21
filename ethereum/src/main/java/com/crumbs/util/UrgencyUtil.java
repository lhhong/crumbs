package com.crumbs.util;

/**
 * Created by low on 21/2/17 12:01 AM.
 */
public class UrgencyUtil {

	private static final double PERFECT_EXCESS_PERCENTAGE = 0.2;
	private static final double PERFECT_DISPOSAL_PERCENTAGE = 0.2;

	public static double getPerfectExcess() {
		return PERFECT_EXCESS_PERCENTAGE;
	}

	public static double getPerfectDisposal() {
		return PERFECT_DISPOSAL_PERCENTAGE;
	}

	private static double rawPercentage(int top, int bottom) {
		return  ((double) top) / ((double) bottom);
	}

	public static int percentageExtra(int initial, int excess) {
		return ((int) (100 * rawPercentage(excess, initial)));
	}

	public static double shortageUrg(int demand, int predictedStock) {
		return rawPercentage(predictedStock, demand);
	}

	public static double excessUrg(int initialDispose, int actualDispose) {
		return rawPercentage(actualDispose, initialDispose);
	}

	public static String getShortageUrgencyLevel(double urgency) {
		if (urgency < -0.1) {
			return "red";
		}
		else if (urgency < 0) {
			return "orange";
		}
		else if (urgency < 0.1) {
			return "yellow";
		}
		else {
			return "green";
		}
	}
	public static String getExcessUrgencyLevel(double urgency) {
		if (urgency > 0.45) {
			return "red";
		}
		else if (urgency > 0.38) {
			return "orange";
		}
		else if (urgency > 0.3) {
			return "yellow";
		}
		else {
			return "green";
		}
	}
}
