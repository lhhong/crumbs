package com.crumbs.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by low on 18/2/17 12:30 PM.
 */
public class DateUtil {

	public static Date toDate(Date date) {
		return toDate(toLocalDate(date));
	}

	public static Date toDate(LocalDate localDate) {
		return new Date(localDate.atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli());
	}

	public static LocalDate toLocalDate(Date date) {
		return LocalDate.from(date.toInstant().atZone(ZoneId.of("GMT")));
	}

	public static Date today() {
		return toDate(todayLocalDate());
	}

	public static LocalDate todayLocalDate() {
		return LocalDate.ofYearDay(2017,50);
	}

	public static Date addDays(Date date, long days) {
		return toDate(toLocalDate(date).plusDays(days));
	}

	public static Date daysFromToday(long days) {
		return toDate(todayLocalDate().plusDays(days));
	}
}
