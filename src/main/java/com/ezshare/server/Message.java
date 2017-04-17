package com.ezshare.server;

import java.io.IOException;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
	
	public String jsonMessage;
	public String command;
	public Resource resource;
	public String secret;
	public String relay;
	
	public Message() { }
	
	public Message(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}
	
	public Message getObject() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
    	return mapper.readValue(this.jsonMessage, this.getClass());
	}
}
