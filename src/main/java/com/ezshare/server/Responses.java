package com.ezshare.server;

import com.ezshare.Constant;
import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Responses {
	
	public Responses() {
		this.response = Constant.SUCCESS;
	}
	
	public Responses(String errorMessage) {
		this.response = Constant.ERROR;
		this.errorMessage = errorMessage;
	}
	
	@JsonView(Views.Response.class)
	public String response;

	@JsonView(Views.ErrorMessage.class)
	public String errorMessage;

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		if (response.equals(Constant.SUCCESS)) {
			return mapper.writerWithView(Views.Response.class).writeValueAsString(this);
		} else {
			return mapper.writerWithView(Views.ErrorMessage.class).writeValueAsString(this);
		}
	}
}
