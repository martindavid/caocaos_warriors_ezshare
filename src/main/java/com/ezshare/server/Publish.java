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
			return Utilities.getReturnMessage(4);
		}

		boolean isValid = validateResource(res);
		if (!isValid) {
			return Utilities.getReturnMessage(2);
		}

		for (Resource localRes : Storage.resourceList) {
			// Check for same primary key and overwrite
			if (localRes.owner.equals(res.owner) && localRes.channel.equals(res.channel)
					&& localRes.uri.equals(res.uri)) {
				localRes.ezserver = res.ezserver;
				localRes.tags = res.tags;
				localRes.description = res.description;
				localRes.name = res.name;
				return Utilities.getReturnMessage(1);
			}
			// Check for primary key differences
			else if (localRes.channel.equals(res.channel) && localRes.uri.equals(res.uri)
					&& !localRes.owner.equals(res.owner)) {
				return Utilities.getReturnMessage(2);
			}
		}

		Storage.resourceList.add(res);
		return Utilities.getReturnMessage(1);
	}

	private boolean validateResource(Resource res) {
		boolean isValid = true;
		// check for empty String
		// Check String values
		if (isContainsWhiteSpace(res.description) || isContainsWhiteSpace(res.name) || isContainsWhiteSpace(res.channel)
				|| isContainsWhiteSpace(res.owner)) {
			isValid = false;
		}
		// Check for present URI
		if (isEmpty(res.uri) && !res.uri.contains("file:")) {
			isValid = false;
		}
		// Check for Owner
		if (res.owner.equals("*")) {
			isValid = false;
		}

		return isValid;
	}

	/***
	 * Code to check if it starts or ends with Whitespace
	 * Reference:http://stackoverflow.com/questions/4067809/how-can-i-find-whitespace-space-in-a-string
	 ***/
	private static boolean isContainsWhiteSpace(final String testCode) {
		if (!testCode.isEmpty() && (Character.isWhitespace(testCode.charAt(0))
				|| Character.isWhitespace(testCode.charAt(testCode.length() - 1)))) {
			return true;
		} else if (!testCode.isEmpty() && testCode.contains("\0")) {
			return true;
		}
		return false;
	}

	private static boolean isEmpty(final String testCode) {
		return (testCode == null || testCode.isEmpty());
	}
}
