package com.crumbs.util;

import com.crumbs.models.Prediction;
import com.crumbs.models.ProductVM;
import com.crumbs.models.RemainingStock;

import java.util.Random;

/**
 * Created by low on 20/2/17 6:09 PM.
 */
public class MockUtil {
	public static Prediction mockPrediction(String item, String cat) {
		ProductVM pro = new ProductVM(item, cat, 2342);

		Prediction p = new Prediction();

		p.setProduct(pro);

		RemainingStock s1 = new RemainingStock();
		s1.setUrgency(3.325);
		s1.setUrgencyLevel("red");
		s1.setQuantity(-4);
		s1.setPercentExtra(-2);
		p.addToStockList(s1);

		RemainingStock s2 = new RemainingStock();
		s2.setUrgency(1.325);
		s2.setUrgencyLevel("yellow");
		s2.setQuantity(24);
		s2.setPercentExtra(16);
		p.addToStockList(s2);

		return p;
	}
}
