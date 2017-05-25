package com.ezshare.server;

import EZShare.Constant;
import EZShare.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.pmw.tinylog.Logger;

public class Share {
	private Resource resource;
	private String secret;
	private String machineSecret;
	private boolean isSecure;

	public Share(Resource resource, String secret, String machineSecret, boolean isSecure) {
		this.secret = secret;
		this.resource = resource;
		this.machineSecret = machineSecret;
		this.isSecure = isSecure;
	}

	public String processResourceMessage() throws IOException {
		Resource res = this.resource;
		if (res == null) {
			return Utilities.getReturnMessage(Constant.MISSING_RESOURCE);
		}
		// Update ezserver value before add to list
		res.ezserver = String.format("%s:%d", Storage.hostName, Storage.port);

		// Validate Secret
		if (this.secret.isEmpty()) {
			Logger.debug("SHARE: secret is empty");
			return Utilities.getReturnMessage(Constant.MISSING_RESOURCE_OR_SECRET);
		}
		if (!this.secret.equals(this.machineSecret)) {
			Logger.debug("SHARE: secret is not match");
			return Utilities.getReturnMessage(Constant.INCORRECT_SECRET);
		}

		// Validate URI
		if (!isValidFileUri(res.uri)) {
			Logger.debug("SHARE: not a valid URI");
			return Utilities.getReturnMessage(Constant.CANNOT_SHARE_RESOURCE);
		} else {

			for (Resource localRes : isSecure ? Storage.secureResourceList : Storage.resourceList) {
				// Check for same primary key and overwrite
				if (localRes.owner.equals(res.owner) && localRes.channel.equals(res.channel)
						&& localRes.uri.equals(res.uri)) {
					Logger.debug("SHARE: update existing resource");
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
				Storage.secureResourceList.add(res);
			} else {
				Storage.resourceList.add(res);
			}

			// If there is any subscriber notify them with this new resource
			if (Storage.subscriber.size() > 0) {
				new Subscription(isSecure).notifySubscriber(res);
			}

			return Utilities.getReturnMessage(Constant.SUCCESS);
		}
	}

	private boolean isValidFileUri(String stringUri) {
		Logger.debug(String.format("SHARE: validate uri - %s", stringUri));
		if (stringUri.isEmpty())
			return false;
		try {
			URI uri = new URI(stringUri);

			if (!uri.getPath().equals("") && uri.isAbsolute() && uri.getScheme().equals(Constant.FILE_SCHEME)) {
				File f = new File(uri.getPath());
				if (!f.isFile()) {
					Logger.debug("SHARE: uri is not a file");
					return false;
				}
			} else {
				return false;
			}
			Logger.debug(String.format("SHARE: uri path: %s", uri.getPath()));
		} catch (URISyntaxException e) {
			Logger.error(e);
			return false;
		}

		return true;
	}

}
