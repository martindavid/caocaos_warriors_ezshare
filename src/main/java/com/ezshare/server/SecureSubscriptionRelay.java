package com.ezshare.server;

import org.pmw.tinylog.Logger;

public class SecureSubscriptionRelay {
	private SecureSubscriber subscriber;

	public SecureSubscriptionRelay(SecureSubscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	public void relaySubscription() {
		Logger.debug("[SUBSCRIPTION_RELAY] START Subscription Relay");
		try {
			if (Storage.secureServerThread.size() == 0) {
				for (Server server : Storage.secureServerList) {
					Logger.debug("[SUBSCRIPTION_RELAY] Start new thread for " + server.hostname + ":" + server.port);
					SubscriptionSecureServerThread serverThread = new SubscriptionSecureServerThread(server, subscriber);
					Thread thread = new Thread(serverThread);
					thread.start();
					Storage.secureServerThread.add(serverThread);
				}
			} else {
				for (SubscriptionSecureServerThread server : Storage.secureServerThread) {
					server.updateRequest(subscriber);
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
