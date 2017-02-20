package com.crumbs.util;

import com.crumbs.entities.BasicTx;
import com.crumbs.entities.Product;
import com.crumbs.entities.TxAccepted;
import com.crumbs.entities.TxSent;
import com.crumbs.models.*;

import java.math.BigInteger;

/**
 * Created by low on 7/2/17 9:57 AM.
 */
public class CrumbsUtil {
	public static BigInteger etherToWei(long ether) {
		return BigInteger.valueOf(ether).multiply(new BigInteger("1000000000000000000"));
	}

	public static long weiToEther(BigInteger wei) {
		return wei.divide(new BigInteger("1000000000000000000")).longValueExact();
	}

	public static TransactionVM toTxVM(BasicTx tx) {
		TransactionVM vm = new TransactionVM();
		vm.setUuid(tx.getUuid());
		vm.setItem(tx.getItem());
		vm.setExpiry(tx.getExpiry());
		vm.setQuantity(tx.getQuantity());
		vm.setSell(tx.isSell());
		vm.setPrice(tx.getPrice());
		vm.setTransportPrice(tx.getTransportPrice());
		vm.setTxDate(tx.getTxDate());
		if (tx instanceof TxAccepted) {
			vm.setSender(((TxAccepted) tx).getSender());
		}
		else if (tx instanceof TxSent) {
			vm.setAccepter(((TxSent) tx).getAccepter());
		}
		return vm;
	}

	public static ProductVM toProductVM(Product product) {
		return new ProductVM(product.getName(), product.getCategory(), product.getPrice());
	}

}
