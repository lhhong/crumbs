package com.crumbs.services;

import com.crumbs.entities.TxAccepted;
import com.crumbs.models.BasicShortExceVM;
import com.crumbs.models.ExceShipVM;
import com.crumbs.models.RemStockVM;
import com.crumbs.models.TransactionVM;
import com.crumbs.util.CrumbsUtil;
import com.crumbs.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

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

	public List<TransactionVM> getMatchingTx(BasicShortExceVM shortExce) {
		List<TxAccepted> availTxs = txService.getAllAvailTx();
		List<TransactionVM> possibleTx = new ArrayList<>();
		availTxs.forEach(tx -> {
			if (shortExce instanceof RemStockVM) {
				if (tx.isSell() && DateUtil.toLocalDate(tx.getTxDate()).isEqual(DateUtil.toLocalDate(((RemStockVM) shortExce).getRequestDate()))
						&& tx.getItem().equalsIgnoreCase(shortExce.getName())) {
					possibleTx.add(CrumbsUtil.toTxVM(tx));
				}
			}
			else if (shortExce instanceof ExceShipVM) {
				if (!tx.isSell() && DateUtil.toLocalDate(tx.getExpiry()).isEqual(DateUtil.toLocalDate(((ExceShipVM) shortExce).getExpiry()))
						&& tx.getItem().equalsIgnoreCase(shortExce.getName())) {
					possibleTx.add(CrumbsUtil.toTxVM(tx));
				}
			}
			else {
				logger.error("ERROR finding instance of BasicShortExce");
			}

		});

		//TODO calculate transport cost and rank
		possibleTx.forEach(tx -> tx.setTransportPrice(optimize.calcTransportCost(tx)));
		possibleTx.sort(Collections.reverseOrder(Comparator.comparingDouble(this::getRank)));
		return possibleTx;

	}

	private double getRank(TransactionVM tx) {
		return 1.0d;
	}
}
