package com.crumbs.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 21/2/17 12:01 AM.
 */
public class UrgencyUtil {

	private static final double PERFECT_PERCENTAGE = 0.15;
	private static final double MULTIPLIER = 4;

	private static double rawPercentage(int initial, int excess) {
		return  ((double) excess) / ((double) initial);
	}

	public static int percentageExtra(int initial, int excess) {
		return ((int) (100 * rawPercentage(initial, excess)));
	}

	private static double sigmoid(double x, int alpha) {
		return (1d / (1 + Math.exp(-alpha * x)));
	}

	public static double shortageUrg(int demand, int predictedStock) {
		return shortageUrgencyFunction(rawPercentage(demand, predictedStock));
	}

	public static double excessUrg(int initialDispose, int actualDispose) {
		return excessUrgencyFunction(rawPercentage(initialDispose, actualDispose));
	}

	private static double excessUrgencyFunction(double x) {
		x = (x - PERFECT_PERCENTAGE) * MULTIPLIER;
		return ((x*x/6) * sigmoid(x, 6));
	}

	private static double shortageUrgencyFunction(double x) {
		x = (x - PERFECT_PERCENTAGE) * MULTIPLIER;
		return ((x* x) * (1- sigmoid(x, 4)));
	}

	public static String getUrgencyLevel(double urgency) {
		List<Double> threshold = calcThreshold();
		if (urgency > threshold.get(0)) {
			return "red";
		}
		if (urgency > threshold.get(1)) {
			return "orange";
		}
		if (urgency > threshold.get(2)) {
			return "yellow";
		}
		else {
			return "green";
		}
	}

	public static List<Double> calcThreshold() {
		List<Double> threshold = new ArrayList<>();
		double severeUrgency = shortageUrgencyFunction(0d);
		threshold.add(severeUrgency);
		threshold.add(severeUrgency*2/3);
		threshold.add(severeUrgency/3);
		return threshold;
	}
}
