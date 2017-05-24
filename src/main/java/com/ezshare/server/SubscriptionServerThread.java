package com.ezshare.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.ezshare.client.UnsubscribeMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

import EZShare.Constant;
import EZShare.Resource;
import EZShare.ResourceTemplate;

public class SubscriptionServerThread implements Runnable {

	public Resource resourceTemplate;
	public Server server;
	private Socket socket;
	private DataInputStream streamIn;
	private DataOutputStream streamOut;

	public SubscriptionServerThread(Server server, Subscriber subscriber) {
		this.server = server;
		this.resourceTemplate = subscriber.subscribeTemplate;
		try {
			socket = new Socket(server.hostname, server.port);
			streamIn = new DataInputStream(socket.getInputStream());
			streamOut = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	@Override
	public void run() {
		try {
			String response = "";

			EZShare.Message message = new EZShare.Message();
			message.command = Constant.SUBSCRIBE.toUpperCase();
			message.relay = false;
			message.resourceTemplate = new ResourceTemplate(this.resourceTemplate);
			streamOut.writeUTF(message.toJson());

			// Save for the first time to SubscriptionResource
			Storage.subscriptionResources.add(new SubscriptionResources(this.resourceTemplate));

			while (true) {
				if (Storage.subscriber.size() == 0) {
					UnsubscribeMessage unmessage = new UnsubscribeMessage(message.id);
					streamOut.writeUTF(unmessage.toJson());
					Storage.serverThread.remove(this);
					break;
				}
				if (streamIn.available() > 0) {
					response = streamIn.readUTF();
					Logger.info(response);
					if (!response.contains(Constant.SUCCESS)) {
						Resource res = Utilities.convertJsonToObject(response, Resource.class);

						for (Subscriber subscriber : Storage.subscriber) {
							try {
								SubscriptionResources subsResource = Storage.subscriptionResources.stream()
										.filter(x -> x.resourceTemplate.equals(subscriber.subscribeTemplate)).findAny()
										.orElse(null);
								if (subsResource != null) {
									subsResource.resources.add(res);
								}

								if (Utilities.isResourceMatch(res, subscriber.subscribeTemplate)) {
									subscriber.resultSize += 1;
									subscriber.streamOut.writeUTF(response);
								}
							} catch (Exception e) {
								Logger.error(e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		} finally {
			Logger.debug("FINISH Thread for Server " + server.hostname + ":" + server.port);
			try {
				if (this.socket != null && !this.socket.isClosed()) {
					this.socket.close();
				}
				if (this.streamIn != null) {
					this.streamIn.close();
				}
				if (this.streamOut != null) {
					this.streamOut.close();
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	/***
	 * Update this thread if there is any new subscriber Check if the existing
	 * subscriber has same template so no need to send new request, otherwise
	 * send new SUBSCRIBE command with new template
	 * 
	 * @param subscriber
	 */
	public void updateRequest(Subscriber subscriber) {
		try {
			Logger.debug(String.format("Update new subscriber for subscription server thread %s:%d", server.hostname, server.port));
			// Find from existing resource list
			// If template exists then no need to send new request to other server just stream out the resources to client
			SubscriptionResources existingTemplate = Storage.subscriptionResources.stream()
					.filter(x -> x.resourceTemplate.equals(subscriber.subscribeTemplate)).findAny().orElse(null);
			if (existingTemplate == null) {
				Logger.debug(String.format("Send new subscribe template to server %s:%d", server.hostname, server.port));
				Storage.subscriptionResources.add(new SubscriptionResources(subscriber.subscribeTemplate));
				EZShare.Message message = new EZShare.Message();
				message.command = Constant.SUBSCRIBE.toUpperCase();
				message.relay = false;
				message.resource = new Resource(subscriber.subscribeTemplate);
				streamOut.writeUTF(message.toJson());
			} else {
				SubscriptionResources subsResource = Storage.subscriptionResources.stream()
						.filter(x -> x.resourceTemplate.equals(subscriber.subscribeTemplate)).findAny().orElse(null);
				if (subsResource != null) {
					for (Resource res : subsResource.resources) {
						subscriber.streamOut.writeUTF(res.toJson());
						subscriber.resultSize += 1;
					}
				}
			}
		} catch (JsonProcessingException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
