package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by low on 17/2/17 10:25 PM.
 */
@Getter
@Setter
public class StockUpdate {

	private int currentQuantity = 0;
	private int stock = 0;
	private int disposed = 0;

	public void stockUp(int stock) {
		currentQuantity += stock;
		this.stock += stock;
	}

	public void dispose(int disposed) {
		this.disposed += disposed;
	}

	public void addQuantity(int quantity) {
		currentQuantity += quantity;
	}
}

