package com.ezshare.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EZShare.Constant;

public class UnsubscribeMessage {
	public String command;
	public String id;

	public UnsubscribeMessage(String id) {
		this.command = Constant.UNSUBSCRIBE.toUpperCase();
		this.id = id;
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

}
