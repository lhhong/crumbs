package com.crumbs.util;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Created by low on 20/2/17 6:10 PM.
 */
public class FoodList {

	public static Map<String, String> all;

	public static Map<String, String> allFood() {
		if (all == null) {
			Map<String, String> map = new HashMap<>();
			map.put("Apple", "Fruits");
			map.put("Orange", "Fruits");
			map.put("Daisy Milk", "Dairy");
			map.put("Yogurt", "Dairy");
			all = map;
		}
		return all;
	}
}
