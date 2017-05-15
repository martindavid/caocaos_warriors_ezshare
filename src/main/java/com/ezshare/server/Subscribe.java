package com.ezshare.server;

import java.util.ArrayList;
import java.util.Arrays;
import org.pmw.tinylog.Logger;

import EZShare.Constant;
import EZShare.Message;
import EZShare.Resource;
import EZShare.ResourceTemplate;

public class Subscribe {
	private Resource resource;
	private boolean relay;
	private String id;

	public Subscribe(Resource resource, boolean relay, String id) {
		this.resource = resource;
		this.relay = relay;
		this.id=id;
	}

	public ArrayList<Resource> getResourceList() {
		ArrayList<Resource> result = new ArrayList<Resource>();
		for (Resource res : Storage.resourceList) {

			// Copy res object because we need to change owner into * if it
			// contains owner
			Resource newRes = new Resource(res);
			if (Utilities.isMatch(newRes, this.resource)) {
				if (!newRes.owner.isEmpty()) {
					newRes.owner = "*";
				}
				Logger.debug(String.format("SUBSCRIBE: Resource Match - Channel: %s, Owner: %s, Uri: %s, Name: %s, Description: %s", res.channel,
						res.owner, res.uri, res.name, res.description));
				result.add(newRes);
			}
		}
		Logger.debug(String.format("SUBSCRIBE: Fetched %d resource from local server", result.size()));

		if (relay) { // Fetch resource from other server
			try {
				// Construct client message to be passed to relay server
				Message clientMessage = new Message();
				clientMessage.command = Constant.SUBSCRIBE.toUpperCase();
				clientMessage.resourceTemplate = new ResourceTemplate(this.resource);
				clientMessage.relay = false;
				Logger.debug("SUBSCRIBE: Client message for relay server");
				Logger.debug(String.format("SUBSCRIBE: Channel: %s, Owner: %s, Uri: %s, Name: %s, Description: %s",
						clientMessage.resourceTemplate.channel, clientMessage.resourceTemplate.owner,
						clientMessage.resourceTemplate.uri, clientMessage.resourceTemplate.name,
						clientMessage.resourceTemplate.description));
				for (ServerList server : Storage.serverList) {
					Logger.debug(String.format("SUBSCRIBE: Fetch resource from %s:%d", server.hostname, server.port));
					ArrayList<Resource> relayRes = new SubscribeRelay(server.hostname, server.port, clientMessage)
							.fetchResourceList();
					Logger.debug(String.format("SUBSCRIBE: Fetched %d resource from %s:%d", relayRes.size(),
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

}

