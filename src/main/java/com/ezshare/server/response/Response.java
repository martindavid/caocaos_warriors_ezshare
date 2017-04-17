package com.ezshare.server.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {
	
	public String response;
	public String errorMessage;
	
//	public Response() { 
//		this.response = ResponseMessage.SUCCESS;
//		this.errorMessage = "";
//	}
//	
//	public Response(String errorMessage) {
//		this.response = ResponseMessage.ERROR;
//		this.errorMessage = errorMessage;
//	}
	
	public String getMessage() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	
		return mapper.writeValueAsString(this);
	}

}
