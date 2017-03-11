package com.crumbs.components;

import com.crumbs.entities.BasicTx;

/**
 * Created by low on 16/2/17 3:03 PM.
 * listener to check if a transaction is included
 */
public interface CheckIncludedListener {
	void onIncluded(BasicTx txIncluded);
}
