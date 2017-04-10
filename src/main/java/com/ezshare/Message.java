package com.ezshare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
	public String command;
	public Resource resource;

	
	public Message() {
		resource = new Resource();
		// Define the default value for resources
		resource.uri = "";
		resource.name="";
		resource.description="";
		resource.owner="";
		resource.channel="";
		
		
	}
	
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}
