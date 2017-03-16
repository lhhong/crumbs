package com.crumbs.services;

import com.alibaba.fastjson.JSON;
import com.crumbs.models.ProductSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by low on 13/3/17 8:53 PM.
 * Use to present chart and table of a product to user
 */
@Service
public class InventoryCacheSrvc {
	private Map<String, ProductSeries> inventoryCache = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(InventoryCacheSrvc.class);

	public ProductSeries getProductSeries(String product) {
		ProductSeries series = inventoryCache.get(product);
		logger.info("got series {}", JSON.toJSONString(series));
		return series;
	}

	public void setCache(String product, List<Integer> startingInventory, List<Integer> stock, List<Integer> demand, List<Integer> ending, List<Integer> disposal) {
		ProductSeries series = new ProductSeries(startingInventory, stock, demand, ending, disposal);
		inventoryCache.put(product, series);
		logger.info("Cached {} with {}", product, JSON.toJSONString(series));
		logger.info("Cached {} with {}", product, JSON.toJSONString(inventoryCache.get(product)));
		logger.info(JSON.toJSONString(inventoryCache.keySet()));
	}
}
