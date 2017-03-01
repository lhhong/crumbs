package com.crumbs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by low on 7/2/17 11:23 AM.
 */
@Service
public class StateUpdater {

	@Autowired
	TransactionService transactionService;

	public void update() {
		transactionService.checkOfferIncluded();
		transactionService.checkAcceptanceIncluded();
		//transactionService.checkAgreeIncluded();
		transactionService.checkOfferAccepted();
		//transactionService.checkAcceptanceAgreed();
	}
}
