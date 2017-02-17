package com.crumbs.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by low on 16/2/17 11:44 PM.
 */
@Getter
@Setter
@AllArgsConstructor
public class ProductVM {
	private String name;
	private String category;
	private long price;
}
