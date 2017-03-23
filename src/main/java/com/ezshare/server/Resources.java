package com.ezshare.server;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	public static Resources fromJson(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		
		Resources parsedValue = null;
		try {
			parsedValue = mapper.readValue(jsonString, Resources.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parsedValue;
	}
}
