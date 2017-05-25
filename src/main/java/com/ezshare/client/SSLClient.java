package com.ezshare.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.pmw.tinylog.Logger;

import com.ezshare.server.Utilities;
import com.ezshare.server.model.Responses;

import EZShare.Constant;
import EZShare.Message;

public class SSLClient {

	private Message message;
	private int portNumber;
	private String hostName;
	private FileTransfer fileTransfer;
	private SSLContext sslContext;

	public SSLClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;

		try (InputStream keyStoreInput = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(Constant.CLIENT_KEYSTORE_KEY);
				InputStream trustStoreInput = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(Constant.CLIENT_TRUSTSTORE_KEY);) {
			this.sslContext = Utilities.setSSLFactories(keyStoreInput, Constant.KEYSTORE_PASSWORD, trustStoreInput);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public void Execute() throws IOException {
		// Create SSL socket and connect it to the remote server
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) this.sslContext.getSocketFactory();
		try (SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(hostName, portNumber);
				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
				DataInputStream streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));) {
			// Print all of the information
			Logger.info("Starting the EZShare Secure Client");
			Logger.info("Connect to host - " + hostName + " on port: " + portNumber);
			Logger.debug("Setting Debug On");
			Logger.debug("[SENT]:" + message.toJson());

			transferMessage(streamOut);
			receiveMessage(streamIn, streamOut);
		} catch (Exception e) {
			Logger.error(e);
		}

	}

	public void transferMessage(DataOutputStream streamOut) throws IOException {
		streamOut.writeUTF(message.toJson());
	}

	public void receiveMessage(DataInputStream streamIn, DataOutputStream streamOut) throws IOException {

		String response = "";
		String string = "";

		boolean exitLoop = false;

		if (message.command.equals(Constant.FETCH.toUpperCase())) {

			while (true) {
				if ((string = DataInputStream.readUTF(streamIn)) != null) {
					response = string;
				}

				Logger.debug(response);
				Responses serverResponse = Utilities.convertJsonToObject(response, Responses.class);

				// Only fetch the file the response is not an error
				if (!serverResponse.response.equals(Constant.ERROR)) {
					// Receiving the file
					fileTransfer = new FileTransfer(streamIn);
					fileTransfer.download();
				}
				exitLoop = true;

				if (exitLoop) {
					break;
				}
			}
		} else if (message.command.equals(Constant.SUBSCRIBE.toUpperCase())) {
			// always listen to server until client explicitly press
			// ENTER
			while (true) {
				if (System.in.available() > 0) {
					break;
				}
				if ((response = DataInputStream.readUTF(streamIn)) != null) {
					Logger.info(response);
					if (response.contains("error")) {
						break;
					}
				}
			}

			UnsubscribeMessage unmessage = new UnsubscribeMessage(message.id);
			streamOut.writeUTF(unmessage.toJson());
			while (true) {
				if ((response = DataInputStream.readUTF(streamIn)) != null) {
					Logger.info(response);
				}
				if ((!message.command.equals(Constant.QUERY.toUpperCase())
						&& !message.command.equals(Constant.SUBSCRIBE.toUpperCase()))
						|| response.contains(Constant.RESULT_SIZE) || response.contains("this")) {
					break;
				}
			}
		} else {
			while (true) {
				if ((string = DataInputStream.readUTF(streamIn)) != null) {
					response = string;
				}
				Logger.debug(response);

				if (!message.command.equals(Constant.QUERY.toUpperCase()) || response.contains(Constant.RESULT_SIZE)) {
					break;
				}
				if (response.contains("error")) {
					break;
				}
			}
		}
	}
}
