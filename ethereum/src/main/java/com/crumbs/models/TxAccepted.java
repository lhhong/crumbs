package com.crumbs.models;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Created by low on 7/2/17 12:19 AM.
 */
public class TxAccepted {

	@Id
	String uuid;
	BigInteger price;
	String item;
	int quantity;
	LocalDateTime expiry;
	boolean isSell;
	@ManyToOne
	@JoinColumn(name = "member")
	Member from;
	BigInteger transportPrice;
	boolean pending;

}
