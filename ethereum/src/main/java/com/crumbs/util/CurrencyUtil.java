package com.crumbs.util;

import java.math.BigInteger;

/**
 * Created by low on 7/2/17 9:57 AM.
 */
public class CurrencyUtil {
	public static BigInteger etherToWei(long ether) {
		return BigInteger.valueOf(ether).multiply(new BigInteger("1000000000000000000"));
	}

	public static long weiToEther(BigInteger wei) {
		return wei.divide(new BigInteger("1000000000000000000")).longValueExact();
	}
}
