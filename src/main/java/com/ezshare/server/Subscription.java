package com.ezshare.server;

import java.io.DataOutputStream;

import org.pmw.tinylog.Logger;

import com.ezshare.server.model.Message;
import com.ezshare.server.model.SecureSubscriber;
import com.ezshare.server.model.Subscriber;
import com.ezshare.server.model.SubscriptionResponse;

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
			if (isSecure) {
				SecureSubscriber subscriber = Storage.secureSubscriber.stream().filter(x -> x.id.equals(message.id))
						.findAny().orElse(null);
				if (subscriber != null) {
					String successResponse = new SubscriptionResponse(Constant.SUCCESS, subscriber.id).toJson();
					streamOut.writeUTF(successResponse);
					
					if (message.relay) {
						SecureSubscriptionRelay relay = new SecureSubscriptionRelay(subscriber);
						relay.relaySubscription();
					}
				}
			} else {
				Subscriber subscriber = Storage.subscriber.stream().filter(x -> x.id.equals(message.id)).findAny()
						.orElse(null);
				if (subscriber != null) {
					String successResponse = new SubscriptionResponse(Constant.SUCCESS, subscriber.id).toJson();
					streamOut.writeUTF(successResponse);

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
			if (this.isSecure) {
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
		if (this.isSecure) {
			for (SecureSubscriber subscriber : Storage.secureSubscriber) {
				Logger.debug(String.format("Notify subscriber %s", subscriber.id));
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
				Logger.debug(String.format("Notify subscriber %s", subscriber.id));
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
