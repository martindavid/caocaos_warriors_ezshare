package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.validator.routines.UrlValidator;

public class ShareCommand {
	private Resource resource;
	private String secret;
	private String machineSecret;

	public ShareCommand(Resource resource, String secret, String machineSecret) {
		this.secret = secret;
		this.resource = resource;
		this.machineSecret = machineSecret;
	}

	public boolean uriValidator(String uri) {
		String[] schemes = { "http", "https" };
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (urlValidator.isValid(uri)) {
			// Means this is an URL so not working for URI
			return true;
		} else {
			return false;
		}
	}

	public String processResourceMessage() throws JsonProcessingException {
		Resource res = this.resource;
		if (res == null) {
			return Utilities.getReturnMessage(4);
		}
		String currentSecret = machineSecret;

		// TODO get secret
		// Validate Secret
		if (!this.secret.equals(currentSecret)) {
			return Utilities.getReturnMessage(12);
		}
		if (this.secret.isEmpty()) {
			return Utilities.getReturnMessage(11);
		}
		// Validate URI
		if (uriValidator(res.uri)) {
			return Utilities.getReturnMessage(12);
		} else {
			Publish publish = new Publish(this.resource);
			return publish.processResourceMessage();
		}

	}

}
