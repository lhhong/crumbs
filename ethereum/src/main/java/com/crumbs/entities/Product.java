package com.crumbs.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by low on 2/2/17 11:41 PM.
 */
@Table (name = "products")
@Entity
@Getter
@Setter
public class Product implements Serializable {

	@Id
	private String name;
	private String category;
	private long price;
	@OneToMany (mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<SalesRecord> salesRecord;
	@OneToMany (mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Shipment> shipmentsRecord;
}
