package com.crumbs.services;

import com.crumbs.entities.Member;
import com.crumbs.entities.TxAccepted;
import com.crumbs.models.BasicShortExceVM;
import com.crumbs.models.ExceShipVM;
import com.crumbs.models.RemStockVM;
import com.crumbs.models.TransactionVM;
import com.crumbs.repositories.MemberRepo;
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

	@Autowired
	MemberRepo memberRepo;

	private static final Logger logger = LoggerFactory.getLogger(MatchMakingSrvc.class);

	public List<TransactionVM> getMatchingTx(BasicShortExceVM shortExce) {
		List<TxAccepted> availTxs = txService.getAllAvailTx();
		return getMatchingTx(shortExce, availTxs);
	}

	public List<TransactionVM> getMatchingTx(BasicShortExceVM shortExce, List<TxAccepted> availTxs) {
		List<TransactionVM> possibleTx = new ArrayList<>();
		availTxs.forEach(tx -> {
			if (shortExce instanceof RemStockVM) {
				if (tx.isSell() && DateUtil.toLocalDate(tx.getExpiry()).isAfter(DateUtil.toLocalDate(((RemStockVM) shortExce).getRequestDate()))
						&& tx.getItem().equalsIgnoreCase(shortExce.getName())) {
					possibleTx.add(CrumbsUtil.toTxVM(tx));
				}
			}
			else if (shortExce instanceof ExceShipVM) {
				if (!tx.isSell() && DateUtil.toLocalDate(tx.getTxDate()).isBefore(DateUtil.toLocalDate(((ExceShipVM) shortExce).getExpiry()))
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
		possibleTx.sort(Collections.reverseOrder(Comparator.comparingDouble((tx) -> getRank(tx, shortExce))));
		return possibleTx;

	}

	private double getRank(TransactionVM tx, BasicShortExceVM shortExce) {
		Member own = memberRepo.findByOwn(true).get(0);
		//TODO use own and tx and shortExce to calculate ranking, the list will then auto sort by ranking
		return 1.0d;
	}
}
