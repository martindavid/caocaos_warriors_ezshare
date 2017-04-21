package com.ezshare;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resource {
	public String name;
	public String description;
	public String[] tags;
	public String uri;
	public String channel;
	public String owner;
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
		this.resourceSize = res.resourceSize;
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
