package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by low on 7/2/17 8:47 PM.
 */
@MappedSuperclass
@Getter
@Setter
public class BasicTx implements Serializable{

	@Id
	String uuid;
	long price;
	String item;
	int quantity;
	Date expiry;
	boolean isSell;
	long transportPrice;
	boolean pending;
	boolean included;
	boolean done;
}
