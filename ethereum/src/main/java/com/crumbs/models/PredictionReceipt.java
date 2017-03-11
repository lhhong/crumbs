package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by low on 23/2/17 12:50 AM.
 * view model for demand predictions coming from python Neural Network predictor
 */
@Getter
@Setter
public class PredictionReceipt {
	private List<Integer> predictions;
}
