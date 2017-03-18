package com.crumbs.util;

/**
 * Created by low on 18/3/17 1:28 PM.
 */
public class TxCancelledException extends Exception {
	public TxCancelledException() {
		super("Tx cancelled");
	}
}
