package com.ezshare.server;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExchangeMessage {

	public String command;
	public ArrayList<ServerList> serverList = new ArrayList<ServerList>();

	public ExchangeMessage(String command, ArrayList<ServerList> serverList) {
		this.command = command;
		this.serverList = serverList;
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
