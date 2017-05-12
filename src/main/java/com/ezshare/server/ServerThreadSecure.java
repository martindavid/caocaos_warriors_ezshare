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
 * 
 * A class to handle thread creation when a new client connected to the server
 * This class also handle the main logic processing
 *
 */
public class ServerThreadSecure extends Thread {

	private String ipAddress;
	private int ID = -1;
	private SSLSocket socket_secure = null;

	public ServerThreadSecure(SSLSocket socket, String ipAddress) throws SocketException {
		this.socket_secure = socket;
		this.ipAddress = ipAddress;
		this.ID = socket.getPort();
	}

	@Override
	public void run() {
		Logger.debug("Server thread " + ID + " running");

		// Create DataInputStream to read input from the client
		DataInputStream streamIn = null;
		try {
			streamIn = new DataInputStream(new BufferedInputStream(socket_secure.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String string = null;
		String jsonString = "unsuccess";
		// Read input from the client and print it to the screen
		try {
			while (true) {
				if ((string = streamIn.readUTF()) != null) {

					jsonString = string;

				}
				System.out.println(jsonString);

				Message message = Utilities.convertJsonToObject(jsonString, Message.class);

				// Create buffered writer to send data to the client
				DataOutputStream streamOut = new DataOutputStream(socket_secure.getOutputStream());

				if ((message.command.equals(Constant.FETCH.toUpperCase())
						|| message.command.equals(Constant.QUERY.toUpperCase()))
						&& !jsonString.contains("resourceTemplate")) {

					streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE_TEMPLATE));
					streamOut.flush();
					break;

				} else if ((message.command.equals(Constant.PUBLISH.toUpperCase())
						|| message.command.equals(Constant.REMOVE.toUpperCase())
						|| message.command.equals(Constant.SHARE.toUpperCase())) && !jsonString.contains("resource")) {

					streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE));
					streamOut.flush();
					break;

				} else {

					CommandHandler handler = new CommandHandler(message, streamOut, Storage.secret, true);
					handler.processMessage();

				}
				Logger.debug(String.format("SERVER: removing %s from ip list", this.ipAddress));
				removeIp(this.ipAddress);
				Logger.debug(String.format("SERVER: ip list size: %d", Storage.ipList.size()));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
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
