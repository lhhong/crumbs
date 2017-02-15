package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by low on 15/2/17 9:16 PM.
 */
@Entity
@Table (name = "sales_records")
@Getter
@Setter
public class SalesRecords implements Serializable{

	@Id
	@GeneratedValue
	private long id;

	private int quantity;
	private Date date;
}
