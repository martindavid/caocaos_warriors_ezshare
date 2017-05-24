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
			// For QUERY, only fetch resource from local server, fetch from
			// remote server will be done from
			// SubscriptionServerThread class
			Query subscribe = new Query(message.resourceTemplate, false,
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
					
					if (message.relay) {
						SecureSubscriptionRelay relay = new SecureSubscriptionRelay(subscriber);
						relay.relaySubscription();
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

					if (message.relay) {
						SubscriptionRelay relay = new SubscriptionRelay(subscriber);
						relay.relaySubscription();
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
		try {
			if (isSecure) {
				SecureSubscriber subscriber = Storage.secureSubscriber.stream().filter(x -> x.id.equals(message.id))
						.findAny().orElse(null);
				Resource resTemplate = subscriber.subscribeTemplate;
				try {
					streamOut.writeUTF("{\"resultSize\":" + subscriber.resultSize + "}");
					if (subscriber.subscriberSocket != null) {
						subscriber.subscriberSocket.close();
					}
					Storage.secureSubscriber.remove(subscriber);
					// Find if there's still any subscriber with same template,
					// if not any then remove subsriptionResources from Storage.subscriptionResources
					SecureSubscriber otherSubscriber = Storage.secureSubscriber.stream()
							.filter(x -> x.subscribeTemplate.equals(resTemplate)).findAny().orElse(null);
					if (otherSubscriber == null) {
						Storage.secureSubscriptionResources.removeIf(x->x.resources.equals(resTemplate));
					}
				} catch (Exception e) {
					Logger.error(e);
				}
			} else {
				Subscriber subscriber = Storage.subscriber.stream().filter(x -> x.id.equals(message.id)).findAny()
						.orElse(null);
				Resource resTemplate = subscriber.subscribeTemplate;
				try {
					streamOut.writeUTF("{\"resultSize\":" + subscriber.resultSize + "}");
					if (subscriber.subscriberSocket != null) {
						subscriber.subscriberSocket.close();
					}
					Storage.subscriber.remove(subscriber);
					// Find if there's still any subscriber with same template,
					// if not any then remove subsriptionResources from Storage.subscriptionResources
					Subscriber otherSubscriber = Storage.subscriber.stream()
							.filter(x -> x.subscribeTemplate.equals(resTemplate)).findAny().orElse(null);
					if (otherSubscriber == null) {
						Storage.subscriptionResources.removeIf(x->x.resources.equals(resTemplate));
					}
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Notify any subscriber for new resources that has been published/shared
	 * 
	 * @param resource
	 *            new resource that come from publish/share command
	 */
	public void notifySubscriber(Resource resource) {
		if (isSecure) {
			for (SecureSubscriber subscriber : Storage.secureSubscriber) {
				if (Utilities.isResourceMatch(resource, subscriber.subscribeTemplate)) {
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
				if (Utilities.isResourceMatch(resource, subscriber.subscribeTemplate)) {
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
