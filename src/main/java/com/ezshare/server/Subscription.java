package com.ezshare.server;

import java.io.DataOutputStream;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import EZShare.Constant;
import EZShare.Resource;

public class Subscription {
	private DataOutputStream streamOut;
	private Message message;
	private boolean isSecure;

	public Subscription() {
	}

	public Subscription(DataOutputStream streamOut, Message message, boolean isSecure) {
		this.streamOut = streamOut;
		this.message = message;
		this.isSecure = isSecure;
	}

	public void subscribe() {
		try {
			Query subscribe = new Query(message.resourceTemplate, message.relay,
					isSecure ? Storage.secureServerList : Storage.serverList);
			ArrayList<Resource> resourceList = subscribe.getResourceList();
			Subscriber subscriber = Storage.subscriber.stream().filter(x -> x.id.equals(message.id)).findAny()
					.orElse(null);

			if (subscriber != null) {
				String successResponse = new Response(Constant.SUCCESS, subscriber.id).toJson();
				streamOut.writeUTF(successResponse);

				if (resourceList.size() > 0) {
					for (Resource res : resourceList) {
						streamOut.writeUTF(res.toJson());
						subscriber.resultSize += 1;
					}
				}
			}

		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public void unsubscribe() {
		Subscriber subscriber = Storage.subscriber.stream().filter(x -> x.id.equals(message.id)).findAny().orElse(null);
		try {
			streamOut.writeUTF("{\"resultSize\":" + subscriber.resultSize + "}");
			Storage.subscriber.remove(subscriber);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public void notifySubscriber(Resource resource) {
		for (Subscriber subscriber : Storage.subscriber) {
			if (Utilities.isResourceMath(resource, subscriber.subscribeTemplate)) {
				Resource newres = new Resource(resource);
				if (!resource.owner.isEmpty()) {
					newres.owner = "*";
				}
				try {
					DataOutputStream streamOut = new DataOutputStream(subscriber.subscriberSocket.getOutputStream());
					streamOut.writeUTF(newres.toJson());
				} catch (Exception e) {
					Logger.error(e);
				}
				subscriber.resultSize += 1;
			}
		}
	}
}
