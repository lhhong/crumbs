package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Created by low on 15/2/17 9:11 PM.
 */
@Table(name = "sales_history")
@Entity
@Getter
@Setter
public class SalesHistory implements Serializable {

	@Id
	private String name;
	@OneToMany(targetEntity = SalesRecords.class)
	private List<SalesRecords>	salesRecords;
}
