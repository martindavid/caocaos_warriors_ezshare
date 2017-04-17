package com.ezshare.server;

import com.ezshare.Resource;
import com.ezshare.server.response.ResourceResponse;
import com.ezshare.server.response.Response;
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
		Boolean isValidResource = validateResource(res);
		
		if (!isValidResource) {
			//return new Response(ResourceResponse.CANT_PUBLISH_RESOURCE).getMessage();
		}
		
		for (Resource resource : Resource.resourceList) {
			// Check for same primary key and overwrite
			if (resource.owner.equals(res.owner) && resource.channel.equals(res.channel)
					&& resource.uri.equals(res.uri)) {
				resource.ezserver = res.ezserver;
				resource.tags = res.tags;
				resource.description = res.description;
				resource.name = res.name;
				//return new Response().getMessage();

			}
			// Check for primary key differences
			else if (resource.channel.equals(res.channel) && resource.uri.equals(res.uri)
					&& !resource.owner.equals(res.owner)) {
				//return new Response(ResourceResponse.CANT_PUBLISH_RESOURCE).getMessage();

			}
		}
		Resource.addResource(res);
		return Utilities.messageReturn(1);
	}
	
	private Boolean validateResource(Resource res) {
		
		Boolean isValid = true;
		
		// check for empty String
		// Check String values
		if (Utilities.containsWhiteSpace(res.description) || Utilities.containsWhiteSpace(res.name)
				|| Utilities.containsWhiteSpace(res.channel) || Utilities.containsWhiteSpace(res.owner)) {
			isValid = false;
		}
		
		// URI must be present and cannot be a file scheme
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
