package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by low on 7/2/17 12:19 AM.
 */
@Table(name = "tx_accepted")
@Entity
@Getter
@Setter
public class TxAccepted {

	@Id
	String uuid;
	BigInteger price;
	String item;
	int quantity;
	Date expiry;
	boolean isSell;
	@ManyToOne
	@JoinColumn(name = "member")
	Member from;
	BigInteger transportPrice;
	boolean pending;
	boolean included;
	boolean done;

}
