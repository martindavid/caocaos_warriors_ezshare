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
	SSLSocket echoSocket;
	SSLSocketFactory sslsocketfactory;

	public SSLClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
	}

	public void Execute() throws IOException {

		// Location of the Java keystore file containing the collection of
		// certificates trusted by this application(trust store).
		System.setProperty("javax.net.ssl.trustStore", "clientKeyStore/clientKeystore.jks");
		System.setProperty("javax.net.debug", "all");

		// Create SSL socket and connect it to the remote server
		sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		echoSocket = (SSLSocket) sslsocketfactory.createSocket(hostName, portNumber);

		// Print all of the information
		Logger.info("Starting the EZShare Server");
		Logger.info("using secret: ");
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port " + portNumber);
		Logger.info("started");

		Logger.debug("Setting Debug On");
		Logger.debug("[SENT]:" + message.toJson());

		transferMessage();

		receiveMessage();

	}

	public void transferMessage() throws IOException {
		try (DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream())) {
			streamOut.writeUTF(message.toJson());
		}
	}

	public void receiveMessage() throws IOException {

		String response = "";
		String string = "";

		try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()))) {

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

					if (!message.command.equals(Constant.QUERY.toUpperCase())
							|| response.contains(Constant.RESULT_SIZE)) {
						break;
					}
					if (response.contains("error")) {
						break;
					}
				}
			}
		} catch (IOException ioe) {
			Logger.error(ioe);
		}
	}
}
