package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by low on 15/2/17 9:16 PM.
 */
@Entity
@Table (name = "sales_record")
@Getter
@Setter
public class SalesRecord implements Serializable{

	@Id
	@GeneratedValue
	private long id;

	private int quantity;
	private LocalDate date;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "product")
	Product product;
}
