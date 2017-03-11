package com.crumbs.models;

import com.crumbs.entities.Member;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by low on 7/2/17 8:31 PM.
 * For front-end use
 */
@Getter
@Setter
public class TransactionVM implements Serializable{

	String uuid;
	long price;
	String item;
	int quantity;
	Date expiry;
	Date txDate;
	boolean sell;
	Member sender;
	Member accepter;
	long transportPrice;
}
