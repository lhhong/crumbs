package com.crumbs.rest;

import com.crumbs.models.Prediction;
import com.crumbs.services.PredictionSrvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by low on 16/2/17 10:33 PM.
 */
@RestController
public class PredictionCtrl {

	private static final Logger logger = LoggerFactory.getLogger(PredictionCtrl.class);

	@Autowired
	PredictionSrvc predictionSrvc;

	@RequestMapping(value = "get_predictions", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Prediction> getPredictions() {
		return predictionSrvc.getAllPredictions();
	}
}
