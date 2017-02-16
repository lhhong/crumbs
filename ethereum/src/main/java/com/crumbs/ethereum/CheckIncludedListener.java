package com.crumbs.ethereum;

import com.crumbs.models.BasicTx;

/**
 * Created by low on 16/2/17 3:03 PM.
 */
public interface CheckIncludedListener {
	void onIncluded(BasicTx txIncluded);
}
