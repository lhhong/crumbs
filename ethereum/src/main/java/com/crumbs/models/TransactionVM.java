package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by low on 7/2/17 8:31 PM.
 */
@Getter
@Setter
public class TransactionVM implements Serializable{

	String uuid;
	long price;
	String item;
	int quantity;
	Date expiry;
	boolean isSell;
	Member sender;
	Member accepter;
	long transportPrice;
}
