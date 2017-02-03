package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by low on 2/2/17 11:41 PM.
 */
@Table (name = "product")
@Entity
@Getter
@Setter
public class Product {

	@GeneratedValue
	@Id
	private long id;
	private String name;

}
