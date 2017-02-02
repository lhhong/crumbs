package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by low on 2/2/17 12:01 PM.
 */
@Getter
@Setter
@Entity
@Table (name = "test")
public class Test implements Serializable {
	@Id
	private long id;
	private String myString;
}
