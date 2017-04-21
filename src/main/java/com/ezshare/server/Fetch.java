package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Fetch {
	private Resource resource;

	public Fetch(Resource resource) {
		this.resource = resource;
	}

	public FetchResponse proceFetch() throws JsonProcessingException {
		Resource res = this.resource;

		for (Resource resourcetest : Storage.resourceList) {
			if (res.uri.equals(resourcetest.uri) && res.channel.equals(resourcetest.channel)) {
				FetchResponse resp = new FetchResponse(Utilities.getReturnMessage(1), resourcetest);
				return resp;
			} else {
				FetchResponse resp = new FetchResponse(Utilities.getReturnMessage(1));
				return resp;
			}
		}
		return null;
	}
}
