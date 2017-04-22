package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Fetch {
	private Resource resource;

	public Fetch(Resource resource) {
		this.resource = resource;
	}

	public FetchResponse proceFetch() throws JsonProcessingException {

		for (Resource res : Storage.resourceList) {
			if (this.resource.uri.equals(res.uri) && this.resource.channel.equals(res.channel)) {
				FetchResponse response = new FetchResponse(Utilities.getReturnMessage(1), res);
				return response;
			}
		}
		FetchResponse response = new FetchResponse(Utilities.getReturnMessage(1));
		return response;
	}
}
