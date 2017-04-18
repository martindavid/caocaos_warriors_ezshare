package com.ezshare.server;

import java.io.IOException;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Responses {
	@JsonView(Views.response.class)
	public String response;
	
	@JsonView(Views.errorMessage.class)
	public String errorMessage;
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		if(response.equals("success")){
			return mapper.writerWithView(Views.response.class).writeValueAsString(this);
		}else{
			return mapper.writerWithView(Views.errorMessage.class).writeValueAsString(this);
		}
	}
	
	public static Responses fromJson(String jsonString) throws 
				JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.readValue(jsonString, Responses.class);
	}

}
