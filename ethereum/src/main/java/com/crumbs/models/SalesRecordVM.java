package com.crumbs.models;

import com.crumbs.entities.SalesRecord;
import com.crumbs.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by low on 17/2/17 10:35 PM.
 * Used when receiving mock data from python script
 */
@Getter
@Setter
public class SalesRecordVM {

	public SalesRecordVM(SalesRecord record) {
		this.quantity = record.getQuantity();
		this.dateStamp = DateUtil.toLocalDate(record.getDateStamp());
	}

	private int quantity;
	private LocalDate dateStamp;
}
