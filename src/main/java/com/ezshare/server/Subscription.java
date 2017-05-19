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

	public Subscription(boolean isSecure) {
		this.isSecure = isSecure;
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

			if (isSecure) {
				SecureSubscriber subscriber = Storage.secureSubscriber.stream().filter(x -> x.id.equals(message.id))
						.findAny().orElse(null);
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
			} else {
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
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Remove the subscriber from the list and close the socket connection
	 */
	public void unsubscribe() {
		if (isSecure) {
			SecureSubscriber subscriber = Storage.secureSubscriber.stream().filter(x -> x.id.equals(message.id))
					.findAny().orElse(null);
			try {
				streamOut.writeUTF("{\"resultSize\":" + subscriber.resultSize + "}");
				if (subscriber.subscriberSocket != null) {
					subscriber.subscriberSocket.close();
				}
				Storage.subscriber.remove(subscriber);
			} catch (Exception e) {
				Logger.error(e);
			}
		} else {
			Subscriber subscriber = Storage.subscriber.stream().filter(x -> x.id.equals(message.id)).findAny()
					.orElse(null);
			try {
				streamOut.writeUTF("{\"resultSize\":" + subscriber.resultSize + "}");
				if (subscriber.subscriberSocket != null) {
					subscriber.subscriberSocket.close();
				}
				Storage.subscriber.remove(subscriber);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * Notify any subscriber for new resources that has been published/shared
	 * @param resource new resource that come from publish/share command
	 */
	public void notifySubscriber(Resource resource) {
		if (isSecure) {
			for (SecureSubscriber subscriber : Storage.secureSubscriber) {
				if (Utilities.isResourceMath(resource, subscriber.subscribeTemplate)) {
					Resource newres = new Resource(resource);
					if (!resource.owner.isEmpty()) {
						newres.owner = "*";
					}
					try {
						DataOutputStream streamOut = new DataOutputStream(
								subscriber.subscriberSocket.getOutputStream());
						streamOut.writeUTF(newres.toJson());
					} catch (Exception e) {
						Logger.error(e);
					}
					subscriber.resultSize += 1;
				}
			}
		} else {
			for (Subscriber subscriber : Storage.subscriber) {
				if (Utilities.isResourceMath(resource, subscriber.subscribeTemplate)) {
					Resource newres = new Resource(resource);
					if (!resource.owner.isEmpty()) {
						newres.owner = "*";
					}
					try {
						DataOutputStream streamOut = new DataOutputStream(
								subscriber.subscriberSocket.getOutputStream());
						streamOut.writeUTF(newres.toJson());
					} catch (Exception e) {
						Logger.error(e);
					}
					subscriber.resultSize += 1;
				}
			}
		}
	}
}
