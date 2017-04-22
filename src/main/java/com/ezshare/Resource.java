package com.ezshare;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resource {
	@JsonView(Views.Common.class)
	public String name;
	@JsonView(Views.Common.class)
	public String description;
	@JsonView(Views.Common.class)
	public String[] tags;
	@JsonView(Views.Common.class)
	public String uri;
	@JsonView(Views.Common.class)
	public String channel;
	@JsonView(Views.Common.class)
	public String owner;
	@JsonView(Views.Common.class)
	public String ezserver;
	@JsonView(Views.FileReceive.class)
	public long resourceSize;

	/**
	 * Resource constructor, set default value for all properties
	 */
	public Resource() {
		this.uri = "";
		this.name = "";
		this.description = "";
		this.owner = "";
		this.channel = "";
		this.tags = new String[0];
	}

	/***
	 * Copy Constructor to deep copy it
	 * 
	 * @param res
	 */
	public Resource(Resource res) {
		this.name = res.name;
		this.description = res.description;
		this.uri = res.uri;
		this.channel = res.channel;
		this.owner = res.owner;
		this.ezserver = res.ezserver;
		this.tags = res.tags;
	}

	public String toFetchResultJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithView(Views.FileReceive.class).writeValueAsString(this);
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithView(Views.Common.class).writeValueAsString(this);
	}
}
