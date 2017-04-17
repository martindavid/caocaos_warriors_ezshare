package com.ezshare;

import java.util.ArrayList;
import org.pmw.tinylog.Logger;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
	@JsonView(Views.comm.class)
	public String command;

	@JsonView(Views.Query.class)
	public boolean relay = true;

	@JsonView(Views.Share.class)
	public String secret;

	@JsonView(Views.norm.class)
	public Resource resource;

	@JsonView(Views.Fetch.class)
	public Resource resourceTemplate;

	@JsonView(Views.Exchange.class)
	public ArrayList<Exchange> serverList = new ArrayList<Exchange>();

	public Message() throws JsonProcessingException {
		resource = new Resource();
		resourceTemplate = resource;
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (command.equals("FETCH")) {
				return mapper.writerWithView(Views.Fetch.class).writeValueAsString(this);
			} else if (command.equals("QUERY")) {
				return mapper.writerWithView(Views.Query.class).writeValueAsString(this);
			} else if (command.equals("SHARE")) {
				return mapper.writerWithView(Views.Share.class).writeValueAsString(this);
			} else if (command.equals("EXCHANGE")) {
				return mapper.writerWithView(Views.Exchange.class).writeValueAsString(this);
			} else {
				return mapper.writerWithView(Views.norm.class).writeValueAsString(this);
			}
		} catch (JsonProcessingException e) {
			Logger.error(e);
		}

		return "";
	}
}
