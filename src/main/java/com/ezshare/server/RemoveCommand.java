package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RemoveCommand {

	private Resource resource;

	public RemoveCommand(Resource resource) {
		this.resource = resource;
	}

	/***
	 * Deletes a Resource Object when the conditions meet
	 * 
	 * @param resJson
	 * @return
	 * @throws JsonProcessingException
	 */
	public String processResource() throws JsonProcessingException {
		Resource res = this.resource;
		if (res.owner.contains("*")) {
			return Utilities.getReturnMessage(3);
		}
		for (Resource localRes : Storage.resourceList) {

			if (localRes.owner.equals(res.owner) && localRes.channel.equals(res.channel)
					&& localRes.uri.equals(res.uri)) {
				Storage.resourceList.remove(localRes);
				return Utilities.getReturnMessage(1);
			}
		}
		return Utilities.getReturnMessage(5);
	}
}
