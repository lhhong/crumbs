package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by low on 2/2/17 11:34 PM.
 */
@Table (name = "transaction")
@Entity
@Getter
@Setter
public class Transactions {

	@Id
	long id;
	boolean isBuy;
	String category;
	String subCat = null;
	String product;
}
