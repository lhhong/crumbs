package com.crumbs.services;

import com.crumbs.models.TransactionVM;
import com.crumbs.entities.TxAccepted;
import com.crumbs.util.CrumbsUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 7/2/17 9:13 PM.
 */
@Service
public class Optimize {

	//TODO work out these algos
	public long calcTransportCost(TransactionVM tx) {
		return 4;
	}

	//TODO to pass in calculated transport cost into it as well
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
