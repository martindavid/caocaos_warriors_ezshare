package com.ezshare.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resources {
	public String name;
	public String description;
	public String[] tags;
	public String uri;
	public String channel;
	public String owner;
	public String ezserver;
	
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	
		return mapper.writeValueAsString(this);
	}
}
