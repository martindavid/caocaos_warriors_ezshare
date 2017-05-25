package com.ezshare.server;

import java.util.ArrayList;
import java.util.Arrays;
import org.pmw.tinylog.Logger;

import com.ezshare.server.model.Server;

import EZShare.Constant;
import EZShare.Message;
import EZShare.Resource;
import EZShare.ResourceTemplate;

public class Query {
	private Resource resource;
	private boolean relay;
	private boolean isSecure;

	public Query(Resource resource, boolean relay, boolean isSecure) {
		this.resource = resource;
		this.relay = relay;
		this.isSecure = isSecure;
	}

	public ArrayList<Resource> getResourceList() {
		ArrayList<Resource> result = new ArrayList<Resource>();
		// if it's secure request, fetch the resources from secureResourcesList,
		// otherwise just from unsecure one
		for (Resource res : isSecure ? Storage.secureResourceList : Storage.resourceList) {

			// Copy res object because we need to change owner into * if it
			// contains owner
			Resource newRes = new Resource(res);
			if (isMatch(newRes, this.resource)) {
				if (!newRes.owner.isEmpty()) {
					newRes.owner = "*";
				}
				result.add(newRes);
			}
		}

		Logger.debug(String.format("QUERY: Fetched %d resource from %s storage from local server", result.size(),
				this.isSecure ? "secure" : "unsecure"));

		if (relay) { // Fetch resource from other server
			try {
				// Construct client message to be passed to relay server
				Message clientMessage = new Message();
				clientMessage.command = Constant.QUERY.toUpperCase();
				clientMessage.resourceTemplate = new ResourceTemplate(this.resource);
				clientMessage.relay = false;
				for (Server server : isSecure ? Storage.secureServerList : Storage.serverList) {
					Logger.debug(String.format("QUERY: Fetch resource from %s:%d", server.hostname, server.port));
					ArrayList<Resource> relayRes = new QueryRelay(server.hostname, server.port, clientMessage)
							.fetchResourceList();
					Logger.debug(String.format("QUERY: Fetched %d resource from %s:%d", relayRes.size(),
							server.hostname, server.port));
					if (relayRes.size() > 0) {
						result.addAll(relayRes);
					}
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}

		return result;
	}

	public static Boolean isMatch(Resource res, Resource template) {
		Boolean result = false;

		if ((res.channel.equals(template.channel)) && (res.name.contains(template.name) || (template.name.isEmpty()))
				&& (res.description.contains(template.description) || (template.description.isEmpty()))
				&& (res.uri.contains(template.uri) || (template.uri.isEmpty()))
				&& (res.owner.contains(template.owner) || (template.owner.isEmpty()))) {
			if (template.tags.length > 0) {
				result = Arrays.asList(res.tags).containsAll(Arrays.asList(template.tags));
			} else {
				result = true;
			}
		}

		return result;
	}

}
