package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Publish {
	private Resource resource;

	public Publish(Resource resource) {
		this.resource = resource;
	}

	/***
	 * Creates a new resource when the conditions are correct
	 * 
	 * @param resJson
	 * @return
	 * @throws JsonProcessingException
	 */
	public String processResourceMessage() throws JsonProcessingException {
		Resource res = this.resource;
		if (res == null) {
			return Utilities.messageReturn(4);
		}

		boolean isValid = validateResource(res);
		if (!isValid) {
			return Utilities.messageReturn(2);
		}

		for (Resource resourceIterator : Resource.resourceList) {
			// Check for same primary key and overwrite
			if (resourceIterator.owner.equals(res.owner) && resourceIterator.channel.equals(res.channel)
					&& resourceIterator.uri.equals(res.uri)) {
				resourceIterator.ezserver = res.ezserver;
				resourceIterator.tags = res.tags;
				resourceIterator.description = res.description;
				resourceIterator.name = res.name;
				return Utilities.messageReturn(1);

			}
			// Check for primary key differences
			else if (resourceIterator.channel.equals(res.channel) && resourceIterator.uri.equals(res.uri)
					&& !resourceIterator.owner.equals(res.owner)) {
				return Utilities.messageReturn(2);

			}
		}
		Resource.addResource(res);
		return Utilities.messageReturn(1);
	}

	public boolean validateResource(Resource res) {
		boolean isValid = true;
		// check for empty String
		// Check String values
		if (Utilities.containsWhiteSpace(res.description) || Utilities.containsWhiteSpace(res.name)
				|| Utilities.containsWhiteSpace(res.channel) || Utilities.containsWhiteSpace(res.owner)) {
			isValid = false;
		}
		// Check for present URI
		if (Utilities.isEmpty(res.uri) && !res.uri.contains("file:")) {
			isValid = false;
		}
		// Check for Owner
		if (res.owner.equals("*")) {
			isValid = false;
		}

		return isValid;
	}
}
