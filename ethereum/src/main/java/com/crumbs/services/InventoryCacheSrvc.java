package com.crumbs.services;

import com.crumbs.models.ProductSeries;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by low on 13/3/17 8:53 PM.
 * Use to present chart of a product to user
 */
@Service
public class InventoryCacheSrvc {
	private Map<String, ProductSeries> inventoryCache;

	public ProductSeries getProductSeries(String product) {
		return inventoryCache.get(product);
	}

	public void setCache(String product, List<Integer> startingInventory, List<Integer> stock, List<Integer> demand, List<Integer> ending, List<Integer> disposal) {
		inventoryCache.put(product, new ProductSeries(startingInventory, stock, demand, ending, disposal));
	}
}
