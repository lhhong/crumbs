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
		Member own = memberRepo.findByOwn(true).get(0);
		List<TransactionVM> possibleTx = new ArrayList<>();
		availTxs.forEach(tx -> {
			if (shortExce instanceof RemStockVM) {
				if (isSuitableSeller(own, shortExce, tx)) {
					possibleTx.add(CrumbsUtil.toTxVM(tx));
				}
			}
			else if (shortExce instanceof ExceShipVM) {
				if (isSuitableBuyer(own, shortExce, tx)) {
					possibleTx.add(CrumbsUtil.toTxVM(tx));
				}
			}
			else {
				logger.error("ERROR finding instance of BasicShortExce");
			}

		});

		//Calculate transport cost and rank
		possibleTx.forEach(tx -> tx.setTransportPrice(optimize.calcTransportCost(tx)));
		possibleTx.sort(Collections.reverseOrder(Comparator.comparingDouble((tx) -> getRank(tx, shortExce))));
		return possibleTx;

	}

	private boolean isSuitableSeller(Member own, BasicShortExceVM shortExce, TxAccepted tx){
		boolean expiryCheck = DateUtil.toLocalDate(tx.getExpiry()).isAfter(DateUtil.toLocalDate(((RemStockVM) shortExce).getRequestDate()));
		boolean itemCheck = tx.getItem().equalsIgnoreCase(shortExce.getName());
		boolean quantityCheck = tx.getQuantity() >= 0.8 * shortExce.getQuantity() && tx.getQuantity() <= 1.2 * shortExce.getQuantity();
		boolean notYourselfCheck = !tx.getSender().equals(own);

		return tx.isSell() && expiryCheck && itemCheck && quantityCheck && notYourselfCheck;
	}

	private boolean isSuitableBuyer(Member own, BasicShortExceVM shortExce, TxAccepted tx){
		boolean expiryCheck = DateUtil.toLocalDate(tx.getTxDate()).isBefore(DateUtil.toLocalDate(((ExceShipVM) shortExce).getExpiry()));
		boolean itemCheck = tx.getItem().equalsIgnoreCase(shortExce.getName());
		boolean quantityCheck = tx.getQuantity() >= 0.8 * shortExce.getQuantity() && tx.getQuantity() <= 1.2 * shortExce.getQuantity();
		boolean notYourselfCheck = !tx.getSender().equals(own);

		return !tx.isSell() && expiryCheck && itemCheck && quantityCheck && notYourselfCheck;
	}

	private double getRank(TransactionVM tx, BasicShortExceVM shortExce) {
		//Member own = memberRepo.findByOwn(true).get(0);
		double rank = 0;
		if (shortExce instanceof RemStockVM) {
			// To rank potential sellers, prefer low price and more days before expiry
			// Assume that each day before expiry is worth $100
			double price = tx.getPrice() + tx.getTransportPrice();
			int daysBeforeExpiry = DateUtil.toLocalDate(tx.getExpiry()).getDayOfYear()-DateUtil.toLocalDate(((RemStockVM) shortExce).getRequestDate()).getDayOfYear();
			rank = 1/(price-100*daysBeforeExpiry); //Take the inverse of price
		}
		else if (shortExce instanceof ExceShipVM) {
			// To rank potential buyers, prefer higher price
			rank = tx.getPrice();
		}

		return rank;
	}
}
