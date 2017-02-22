package com.crumbs.services;

import com.crumbs.entities.TxAccepted;
import com.crumbs.models.ExceShipVM;
import com.crumbs.models.RemStockVM;
import com.crumbs.models.TransactionVM;
import com.crumbs.repositories.TxAcceptedRepo;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by low on 22/2/17 6:21 PM.
 */
@Service
public class MatchMakingSrvc {

	@Autowired
	TransactionService txService;

	@Autowired
	Optimize optimize;

	private static final Logger logger = LoggerFactory.getLogger(MatchMakingSrvc.class);

	public List<TransactionVM> getMatchingTxForShortage(RemStockVM remStockVM) {
		List<TxAccepted> availTxs = txService.getAllAvailTx();
		List<TransactionVM> possibleTx = new ArrayList<>();
		availTxs.forEach(tx -> {
			if (tx.isSell() && DateUtil.toLocalDate(tx.getTxDate()).isEqual(DateUtil.toLocalDate(remStockVM.getRequestDate()))
					&& tx.getItem().equalsIgnoreCase(remStockVM.getName())) {
				possibleTx.add(CrumbsUtil.toTxVM(tx));
			}
		});
		possibleTx.forEach(tx -> tx.setTransportPrice(optimize.calcTransportCost(tx)));
		return possibleTx;
	}

	public List<TransactionVM> getMatchingTxForExcess(ExceShipVM exceShipVM) {
		return null;
	}
}
