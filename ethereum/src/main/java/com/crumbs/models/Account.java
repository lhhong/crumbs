package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by low on 3/2/17 10:38 PM.
 */

@Entity
@Table (name = "account")
@Getter
@Setter
public class Account implements Serializable{

	@Id
	private int id = 1;
	private byte[] privateKey;
}
