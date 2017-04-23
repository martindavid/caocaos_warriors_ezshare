package com.ezshare;

import java.util.ArrayList;
import org.pmw.tinylog.Logger;

import com.ezshare.client.Exchange;
import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
	@JsonView(Views.Command.class)
	public String command;

	@JsonView(Views.Query.class)
	public boolean relay = true;

	@JsonView(Views.Share.class)
	public String secret = "";

	@JsonView(Views.Common.class)
	public Resource resource;

	@JsonView(Views.Fetch.class)
	public ResourceTemplate resourceTemplate;

	@JsonView(Views.Exchange.class)
	public ArrayList<Exchange> serverList = new ArrayList<Exchange>();

	public Message() throws JsonProcessingException {
		resource = new Resource();
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			switch (this.command.toLowerCase()) {
				case Constant.FETCH:
					this.resourceTemplate = new ResourceTemplate(this.resource);
					return mapper.writerWithView(Views.Fetch.class).writeValueAsString(this);
				case Constant.QUERY:
					this.resourceTemplate = new ResourceTemplate(this.resource);
					return mapper.writerWithView(Views.Query.class).writeValueAsString(this);
				case Constant.SHARE:
					return mapper.writerWithView(Views.Share.class).writeValueAsString(this);
				case Constant.EXCHANGE:
					return mapper.writerWithView(Views.Exchange.class).writeValueAsString(this);
				default:
					return mapper.writerWithView(Views.Common.class).writeValueAsString(this);
			}
		} catch (JsonProcessingException e) {
			Logger.error(e);
		}

		return "";
	}
}
