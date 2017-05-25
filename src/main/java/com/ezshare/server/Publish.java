package com.ezshare.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.pmw.tinylog.Logger;

import EZShare.Constant;
import EZShare.Resource;

public class Publish {
	private Resource resource;
	private boolean isSecure;

	public Publish(Resource resource, boolean isSecure) {
		this.resource = resource;
		this.isSecure = isSecure;
	}

	/***
	 * Creates a new resource when the conditions are correct
	 * 
	 * @param resJson
	 * @return
	 * @throws IOException
	 */
	public String processResourceMessage() throws IOException {
		Resource res = this.resource;
		if (res == null) {
			return Utilities.getReturnMessage(Constant.MISSING_RESOURCE);
		}

		boolean isValid = validateResource(res);
		if (!isValid) {
			return Utilities.getReturnMessage(Constant.CANNOT_PUBLISH_RESOURCE);
		}

		// Update ezserver value before add to list
		res.ezserver = String.format("%s:%d", Storage.hostName, Storage.port);
		for (Resource localRes : isSecure ? Storage.secureResourceList : Storage.resourceList) {
			// Check for same primary key and overwrite
			if (localRes.owner.equals(res.owner) && localRes.channel.equals(res.channel)
					&& localRes.uri.equals(res.uri)) {
				localRes.ezserver = res.ezserver;
				localRes.tags = res.tags;
				localRes.description = res.description;
				localRes.name = res.name;
				return Utilities.getReturnMessage(Constant.SUCCESS);
			}
			// Check for primary key differences
			else if (localRes.channel.equals(res.channel) && localRes.uri.equals(res.uri)
					&& !localRes.owner.equals(res.owner)) {
				return Utilities.getReturnMessage(Constant.CANNOT_PUBLISH_RESOURCE);
			}
		}

		if (isSecure) {
			Logger.debug("[PUBLISH] store new resources to Secure storage");
			Storage.secureResourceList.add(res);
		} else {
			Logger.debug("[PUBLISH] store new resources to Unsecure storage");
			Storage.resourceList.add(res);
		}

		// If there are subscribers notify this new published resource
		if (Storage.subscriber.size() > 0) {
			new Subscription(this.isSecure).notifySubscriber(res);
		}
		return Utilities.getReturnMessage(Constant.SUCCESS);
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
		if (!isValidUri(res.uri)) {
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

	private static boolean isValidUri(String stringUri) {
		boolean isValid = true;
		try {
			URI uri = new URI(stringUri);
			isValid = uri != null && uri.isAbsolute() && !uri.getScheme().equals(Constant.FILE_SCHEME);
		} catch (URISyntaxException e) {
			isValid = false;
		}

		return isValid;
	}
}
