package com.crumbs.rest;

import com.crumbs.models.*;
import com.crumbs.services.InventoryCacheSrvc;
import com.crumbs.services.MatchMakingSrvc;
import com.crumbs.services.PredictionSrvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by low on 16/2/17 10:33 PM.
 */
@RestController
public class PredictionCtrl {

	private static final Logger logger = LoggerFactory.getLogger(PredictionCtrl.class);

	@Autowired
	PredictionSrvc predictionSrvc;

	@Autowired
	MatchMakingSrvc matchMakingSrvc;

	@Autowired
	InventoryCacheSrvc inventoryCache;

	@RequestMapping(value = "prediction_qty", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public PredictionQty getPredictionQty() {
		return predictionSrvc.getPredictionQty();
	}

	@RequestMapping(value = "predictions", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public PredictionVM getPredictions() {
		return predictionSrvc.getAndRankPredictions();
	}

	@RequestMapping(value = "product_table", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ProductSeries getProductTableeries(@RequestBody String product) {
		logger.info("get product series for {}", product);
		return inventoryCache.getProductSeries(product);
	}

	@RequestMapping(value = "matchingTxForShortage", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TransactionVM> getMatchingTxForShortage(@RequestBody RemStockVM remStockVM) {
		return matchMakingSrvc.getMatchingTx(remStockVM);
	}

	@RequestMapping(value = "matchingTxForExcess", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TransactionVM> getMatchingTxForExcess(@RequestBody ExceShipVM exceShipVM) {
		return matchMakingSrvc.getMatchingTx(exceShipVM);
	}
}
