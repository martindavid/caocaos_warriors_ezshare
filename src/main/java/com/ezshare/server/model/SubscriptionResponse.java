package com.ezshare.server.model;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscriptionResponse {

	public SubscriptionResponse(String response, String subscriberId) {
		this.id = subscriberId;
		this.response = response;
	}

	@JsonView(Views.Response.class)
	public String response;
	public String id = "";

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithView(Views.Response.class).writeValueAsString(this);
	}
}