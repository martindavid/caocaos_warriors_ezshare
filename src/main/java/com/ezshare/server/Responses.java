package com.ezshare.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Responses {
	public String response;
	public String errorMessage;
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	
		return mapper.writeValueAsString(this);
	}

}
