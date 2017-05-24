package com.ezshare.server;

import org.pmw.tinylog.Logger;

public class SubscriptionRelay {
	private Subscriber subscriber;

	public SubscriptionRelay(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public void relaySubscription() {
		Logger.debug("[SUBSCRIPTION_RELAY] START Subscription Relay");
		try {
			if (Storage.serverThread.size() == 0) {
				for (Server server : Storage.serverList) {
					Logger.debug("[SUBSCRIPTION_RELAY] Start new thread for " + server.hostname + ":" + server.port);
					SubscriptionServerThread serverThread = new SubscriptionServerThread(server, subscriber);
					Thread thread = new Thread(serverThread);
					thread.start();
					Storage.serverThread.add(serverThread);
				}
			} else {
				for (SubscriptionServerThread server : Storage.serverThread) {
					server.updateRequest(subscriber);
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
