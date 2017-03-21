package com.crumbs.services;

import com.crumbs.entities.Member;
import com.crumbs.models.TransactionVM;
import com.crumbs.entities.TxAccepted;
import com.crumbs.repositories.MemberRepo;
import com.crumbs.util.CrumbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 7/2/17 9:13 PM.
 */
@Service
public class Optimize {

	@Autowired
	MemberRepo memberRepo;

	public long calcTransportCost(TransactionVM tx) {
		Member own = memberRepo.findByOwn(true).get(0);
		Member seller = tx.getSender();
		// Calculate the euclidean distance
		long x_diff = own.getX() - seller.getX();
		long y_diff = own.getY() - seller.getY();
		double distance = Math.sqrt(x_diff*x_diff + y_diff*y_diff);

		double flat_hiring_fee = 50;
		double base_fare_rate = 100;
		int fare_increment = 10;
		int base_quantity = 500;

		int diff_quantity = tx.getQuantity() - base_quantity;
		if (diff_quantity > 0){
			base_fare_rate = base_fare_rate + fare_increment*diff_quantity/50;
		}

		double totalCost = flat_hiring_fee + base_fare_rate * distance/5;

		return (long)totalCost;
	}

	public List<TransactionVM> rankOffers(List<TxAccepted> offers) {
		List<TransactionVM> lists = new ArrayList<>();
		for (TxAccepted tx : offers) {
			lists.add(CrumbsUtil.toTxVM(tx));
		}
		for (TransactionVM tx : lists) {
			tx.setTransportPrice(calcTransportCost(tx));
		}
		return lists;
	}
}
