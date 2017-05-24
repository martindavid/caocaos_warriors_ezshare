package com.ezshare.server.model;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExchangeMessage {

	public String command;
	public ArrayList<Server> serverList = new ArrayList<Server>();

	public ExchangeMessage(String command, ArrayList<Server> serverList) {
		this.command = command;
		this.serverList = serverList;
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
