package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by low on 2/2/17 11:41 PM.
 */
@Table (name = "product")
@Entity
@Getter
@Setter
public class Product implements Serializable {

	@GeneratedValue
	@Id
	private long id;

	private String name;
	private String category;
	private long price;
	@OneToMany (mappedBy = "sold_product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<SalesRecord> salesRecords;
	@OneToMany (mappedBy = "shipped_product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Shipment> shipments;
}
