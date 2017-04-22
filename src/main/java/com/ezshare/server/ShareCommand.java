package com.ezshare.server;

import com.ezshare.Constant;
import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class ShareCommand {
	private Resource resource;
	private String secret;
	private String machineSecret;

	public ShareCommand(Resource resource, String secret, String machineSecret) {
		this.secret = secret;
		this.resource = resource;
		this.machineSecret = machineSecret;
	}

	public String processResourceMessage() throws JsonProcessingException {
		Resource res = this.resource;
		if (res == null) {
			return Utilities.getReturnMessage(4);
		}

		// Validate Secret
		if (this.secret.isEmpty()) {
			return Utilities.getReturnMessage(11);
		}
		if (!this.secret.equals(this.machineSecret)) {
			return Utilities.getReturnMessage(12);
		}

		// Validate URI
		if (!isValidFileUri(res.uri)) {
			return Utilities.getReturnMessage(12);
		} else {

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

			// Update ezserver value before add to list
			res.ezserver = String.format("%s:%d", Storage.hostName, Storage.port);
			Storage.resourceList.add(res);
			return Utilities.getReturnMessage(1);
		}
	}

	private boolean isValidFileUri(String stringUri) {
		boolean isValid = true;
		try {
			URI uri = new URI(stringUri);
			if (uri != null && uri.isAbsolute() && uri.getScheme().equals(Constant.FILE_SCHEME)){
				File f = new File(uri.getPath());
				if (!f.isFile()) {
					isValid = false;
				}
			}
		} catch (URISyntaxException e) {
			isValid = false;
		}

		return isValid;
	}

}
