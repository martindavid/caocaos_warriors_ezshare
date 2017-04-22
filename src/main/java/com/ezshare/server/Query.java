package com.ezshare.server;

import java.util.ArrayList;
import java.util.Arrays;
import org.pmw.tinylog.Logger;
import com.ezshare.Resource;
import com.ezshare.Constant;
import com.ezshare.Message;

public class Query {
	private Resource resource;
	private boolean relay;

	public Query(Resource resource, boolean relay) {
		this.resource = resource;
		this.relay = relay;
	}

	public ArrayList<Resource> getResourceList() {
		ArrayList<Resource> result = new ArrayList<Resource>();
		for (Resource res : Storage.resourceList) {

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

		if (relay) { // Fetch resource from other server
			try {
				// Construct client message to be passed to relay server
				Message clientMessage = new Message();
				clientMessage.command = Constant.QUERY.toUpperCase();
				clientMessage.resourceTemplate = this.resource;
				clientMessage.relay = false;
				for (ServerList server : Storage.serverList) {
					Logger.debug("Fetch resource from %s:%d", server.hostname, server.port);
					ArrayList<Resource> relayRes = new QueryRelay(server.hostname, server.port, clientMessage)
							.fetchResourceList();
					Logger.debug(String.format("Fetched %d resource from %s:%d", relayRes.size(), server.hostname,
							server.port));
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

	private Boolean isMatch(Resource res, Resource template) {
		Boolean result = false;

		Logger.debug(String.format("Channel: %s, Owner: %s, Uri: %s, Name: %s, Description: %s", res.channel, res.owner,
				res.uri, res.name, res.description));

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
