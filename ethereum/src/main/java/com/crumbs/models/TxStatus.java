package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by low on 7/2/17 9:46 PM.
 * to pass transaction status info to front-end
 */
@Getter
@Setter
public class TxStatus {
	private List<TransactionVM> successfulOffers = new ArrayList<>();
	private List<TransactionVM> successfulAccepts = new ArrayList<>();
	private List<TransactionVM> pendingOffers = new ArrayList<>();
	private List<TransactionVM> pendingAccepts = new ArrayList<>();
	private List<TransactionVM> pendingAgrees = new ArrayList<>();
	private List<TransactionVM> offersAccepted = new ArrayList<>();
	private List<TransactionVM> doneTx = new ArrayList<>();
}
