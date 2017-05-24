package com.ezshare.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.pmw.tinylog.Logger;

import com.ezshare.server.Responses;
import com.ezshare.server.Utilities;

import EZShare.Constant;
import EZShare.Message;

public class SSLClient {

	private Message message;
	private int portNumber;
	private String hostName;
	private FileTransfer fileTransfer;

	public SSLClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
	}

	public void Execute() throws IOException {

		// Location of the Java keystore file containing the collection of
		// the keystore file contains an application's own certificate and
		// private key
		System.setProperty(Constant.JAVANET_KEYSTORE_PROP, Constant.CLIENT_KEYSTORE_KEY);
		System.setProperty(Constant.JAVANET_KEYSTOREPASS_PROP, Constant.KEYSTORE_PASSWORD);
		// certificates trusted by this application(trust store).
		System.setProperty(Constant.JAVANET_TRUSTSTORE_PROP, Constant.CLIENT_TRUSTSTORE_KEY);

		// Create SSL socket and connect it to the remote server
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try (SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(hostName, portNumber);
				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
				DataInputStream streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));) {
			// Print all of the information
			Logger.info("Starting the EZShare Secure Client");
			Logger.info("Connect to host - " + hostName + " on port: " + portNumber);
			Logger.debug("Setting Debug On");
			Logger.debug("[SENT]:" + message.toJson());

			transferMessage(streamOut);

			receiveMessage(streamIn);
		} catch (Exception e) {
			Logger.error(e);
		}

	}

	public void transferMessage(DataOutputStream streamOut) throws IOException {
		streamOut.writeUTF(message.toJson());
	}

	public void receiveMessage(DataInputStream streamIn) throws IOException {

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
