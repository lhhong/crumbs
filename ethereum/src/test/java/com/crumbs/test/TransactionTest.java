package com.crumbs.test;

import com.alibaba.fastjson.JSON;
import com.crumbs.services.TransactionService;
import com.crumbs.util.UrgencyUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by low on 6/2/17 1:23 PM.
 */
public class TransactionTest {
	private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

	@Test
	public void test() {
		logger.info(JSON.toJSONString(UrgencyUtil.excessUrg(23, -6 )));
	}

	@Test
	public void random() {
		logger.info(LocalDateTime.now(ZoneId.of("GMT")).toString());
	}
}
