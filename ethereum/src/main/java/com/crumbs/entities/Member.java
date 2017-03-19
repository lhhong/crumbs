package com.crumbs.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Arrays;

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
	private String location;
	private long x;
	private long y;
	private boolean own;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Member)) return false;
		Member member = (Member) obj;
		return (member.getName().equals(this.getName()) && Arrays.equals(member.getAddr(), this.getAddr()) &&
				member.getLocation().equals(this.getLocation()) && member.getX() == this.getX() && member.getY() == this.getY() &&
				member.isOwn() == this.isOwn());
	}
}
