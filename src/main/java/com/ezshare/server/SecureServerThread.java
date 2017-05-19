package com.ezshare.server;
//import com.ezshare.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

import javax.net.ssl.SSLSocket;

import org.pmw.tinylog.Logger;

import EZShare.Constant;

/**
 *
 * A class to handle thread creation when a new client connected to the server
 * This class also handle the main logic processing
 *
 */
public class SecureServerThread extends Thread {

	private String ipAddress;
	private int ID = -1;
	private SSLSocket secureSocket = null;

	public SecureServerThread(SSLSocket socket, String ipAddress) throws SocketException {
		this.secureSocket = socket;
		this.ipAddress = ipAddress;
		this.ID = socket.getPort();
	}

	@Override
	public void run() {
		Logger.debug("Server thread " + ID + " running");
		String jsonString = "";
		// Read input from the client and print it to the screen
		try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(secureSocket.getInputStream()));) {
			while (true) {
				try {
					jsonString = streamIn.readUTF();
					Logger.debug(jsonString);
				} catch (Exception e) {
					// No need to log, because this is a hackyway to read value
					// from DataInputStream with SSLSocket
					jsonString = "";
				}

				if (!jsonString.isEmpty()) {
					DataOutputStream streamOut = new DataOutputStream(secureSocket.getOutputStream());
					Message message = Utilities.convertJsonToObject(jsonString, Message.class);
					if ((message.command.equals(Constant.FETCH.toUpperCase())
							|| message.command.equals(Constant.QUERY.toUpperCase()))
							&& !jsonString.contains("resourceTemplate")) {

						streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE_TEMPLATE));
						break;

					} else if ((message.command.equals(Constant.PUBLISH.toUpperCase())
							|| message.command.equals(Constant.REMOVE.toUpperCase())
							|| message.command.equals(Constant.SHARE.toUpperCase()))
							&& !jsonString.contains("resource")) {

						streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE));
						break;
					} else if (message.command.equals(Constant.SUBSCRIBE.toUpperCase())) {
						// store the information for this subscriber
						secureSocket.setKeepAlive(true);
						Storage.secureSubscriber.add(new SecureSubscriber(message.id, 0, message.resourceTemplate, this.secureSocket));
						Subscription subscription = new Subscription(streamOut, message, true);
						subscription.subscribe();
						//break;
					} else {
						CommandHandler handler = new CommandHandler(message, streamOut, Storage.secret, true);
						handler.processMessage();
						break;
					}
				}
			}
			Logger.debug(String.format("SERVER: removing %s from ip list", this.ipAddress));
			removeIp(this.ipAddress);
			Logger.debug(String.format("SERVER: ip list size: %d", Storage.ipList.size()));
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private void removeIp(String ipAddress) {
		ConnectionTracking tracking = (ConnectionTracking) Storage.ipList.stream()
				.filter(x -> x.ipAddress.equals(ipAddress)).findAny().orElse(null);
		if (tracking != null) {
			Storage.ipList.remove(tracking);
		}
	}
}
