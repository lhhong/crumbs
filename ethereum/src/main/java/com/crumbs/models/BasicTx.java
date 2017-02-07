package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by low on 7/2/17 8:47 PM.
 */
@Getter
@Setter
public class BasicTx {

	@Id
	String uuid;
	BigInteger price;
	String item;
	int quantity;
	Date expiry;
	boolean isSell;
	BigInteger transportPrice;
	boolean pending;
	boolean included;
	boolean done;
}
