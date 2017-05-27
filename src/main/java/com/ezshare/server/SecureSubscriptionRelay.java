package com.ezshare.server;

import org.pmw.tinylog.Logger;

import com.ezshare.server.model.SecureSubscriber;
import com.ezshare.server.model.Server;

public class SecureSubscriptionRelay {
	private SecureSubscriber subscriber;

	public SecureSubscriptionRelay(SecureSubscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	public void relaySubscription() {
		Logger.debug("[SUBSCRIPTION_RELAY] START Subscription Relay");
		try {
			if (Storage.subscriptionSecureServerThread.size() == 0) {
				for (Server server : Storage.secureServerList) {
					Logger.debug("[SUBSCRIPTION_RELAY] Start new thread for " + server.hostname + ":" + server.port);
					SubscriptionSecureServerThread serverThread = new SubscriptionSecureServerThread(server, subscriber);
					Thread thread = new Thread(serverThread);
					thread.start();
					Storage.subscriptionSecureServerThread.add(serverThread);
				}
			} else {
				for (SubscriptionSecureServerThread server : Storage.subscriptionSecureServerThread) {
					server.updateRequest(subscriber);
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
