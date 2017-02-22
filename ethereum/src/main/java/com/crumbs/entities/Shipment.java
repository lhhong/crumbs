package com.crumbs.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by low on 15/2/17 9:11 PM.
 */
@Table(name = "shipment")
@Entity
@Getter
@Setter
public class Shipment implements Serializable {

	@Id
	@GeneratedValue
	long id;

	private Date dateStamp;
	private Date expiry;
	private int	quantity;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "product")
	Product product;
}
