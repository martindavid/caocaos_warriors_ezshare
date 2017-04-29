package com.ezshare.server;

import com.fasterxml.jackson.core.JsonProcessingException;

import EZShare.Constant;
import EZShare.Resource;

public class Remove {

	private Resource resource;

	public Remove(Resource resource) {
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
			return Utilities.getReturnMessage(Constant.INVALID_RESOURCE);
		}

		Resource existingRes = (Resource) Storage.resourceList.stream()
				.filter(x -> x.owner.equals(res.owner) && x.channel.equals(res.channel) && x.uri.equals(res.uri))
				.findAny().orElse(null);
		
		if (existingRes != null) {
			Storage.resourceList.remove(existingRes);
			return Utilities.getReturnMessage(Constant.SUCCESS);
		} else {
			return Utilities.getReturnMessage(Constant.CANNOT_REMOVE_RESOURCE);
		}
	}
}
