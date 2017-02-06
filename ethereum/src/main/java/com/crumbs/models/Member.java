package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by low on 6/2/17 12:51 AM.
 */
@Table(name = "member")
@Entity
@Getter
@Setter
public class Member implements Serializable {

	private byte[] addr;
	private String name;
	private long x;
	private long y;
}
