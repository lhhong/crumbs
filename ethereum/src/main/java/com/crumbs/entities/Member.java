package com.crumbs.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by low on 6/2/17 12:51 AM.
 * A member defined in the solidity contract
 */
@Table(name = "member")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member implements Serializable {

	@Id
	private byte[] addr;
	private String name;
	private long x;
	private long y;
	private boolean own;
}
