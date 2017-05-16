package com.ezshare.server;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EZShare.Constant;

public class Response {

	public Response() {
		this.response = Constant.SUCCESS;
	}
	
	public Response(String response, String subscriberId) {
		this.id = subscriberId;
		this.response = response;
	}

	public Response(String errorMessage) {
		this.response = Constant.ERROR;
		this.errorMessage = errorMessage;
	}

	@JsonView(Views.Response.class)
	public String response;
	public String id = "";

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