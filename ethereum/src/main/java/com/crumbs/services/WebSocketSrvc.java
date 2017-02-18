package com.crumbs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Created by low on 18/2/17 6:08 PM.
 */
public class WebSocketSrvc {
	@Autowired
	SimpMessagingTemplate messagingTemplate;

	public void sendBalance(long balance) {
		String url = "/topics/balance";
		messagingTemplate.convertAndSend(url, balance);
	}
}
