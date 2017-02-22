package com.crumbs.services;

import com.crumbs.models.ExceShipVM;
import com.crumbs.models.RemStockVM;
import com.crumbs.models.TransactionVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by low on 22/2/17 6:21 PM.
 */
@Service
public class MatchMakingSrvc {

	private static final Logger logger = LoggerFactory.getLogger(MatchMakingSrvc.class);

	public List<TransactionVM> getMatchingTxForShortage(RemStockVM remStockVM) {
		return null;
	}

	public List<TransactionVM> getMatchingTxForExcess(ExceShipVM exceShipVM) {
		return null;
	}
}
