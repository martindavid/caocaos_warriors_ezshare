package com.ezshare.server;
//import com.ezshare.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.client.FileTransfer;
import com.ezshare.Resource;

/**
 * @author mvalentino
 * 
 *         A class to handle thread creation when a new client connected to the
 *         server This class also handle the main logic processing
 *
 */
public class ServerThread extends Thread {
	private Socket socket = null;
	private int ID = -1;
	private String secret;

	public ServerThread(Socket socket, String secret, int connIntervalLimit) throws SocketException {
		this.socket = socket;
		this.socket.setSoTimeout(connIntervalLimit * 1000);;
		this.secret = secret;
		this.ID = socket.getPort();
	}

	@Override
	public void run() {
		Logger.debug("Server thread " + ID + " running");
		String message = "";
		try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());) {
			while (true) {
				if (streamIn.available() > 0) {
					message = streamIn.readUTF();

					Logger.debug(message);

					Message messageObject = Utilities.convertJsonToObject(message, Message.class);
					CommandHandler handler = new CommandHandler(messageObject, this.secret);
					String responseMessage = "";

					if (messageObject.command.equals(Constant.FETCH.toUpperCase())) {
						if (!message.contains("resourceTemplate")) {
							responseMessage = Utilities.getReturnMessage(8);
						} else {
							Fetch fetch = new Fetch(messageObject.resourceTemplate);
							FetchResponse fetchsponse = fetch.proceFetch();
							if (messageObject.resourceTemplate.uri != "" && fetchsponse != null) {
								String resMess = fetchsponse.getResponseMessage();
								streamOut.writeUTF(resMess);
								Resource resp = fetchsponse.getResource();
								if (resp != null) {
									FileTransfer file = new FileTransfer(streamIn, streamOut,
											messageObject.resourceTemplate.uri);
									resp.resourceSize = file.getFileSize();
									streamOut.writeUTF(resp.toJson());
									file.send();
								}
								responseMessage = fetchsponse.adsize.toJson();
							} else {
								responseMessage = Utilities.getReturnMessage(7);
							}
							streamOut.writeUTF(responseMessage);
							break;
						}
					} else if (messageObject.command.equals(Constant.QUERY.toUpperCase())) {
						Query query = new Query(messageObject.resourceTemplate, messageObject.relay);
						ArrayList<Resource> resourceList = query.getResourceList();
						String successResponse = new Responses().toJson();
						streamOut.writeUTF(successResponse);
						if (resourceList.size() > 0) {
							for (Resource res : resourceList) {
								streamOut.writeUTF(res.toJson());
							}
						}
						streamOut.writeUTF("{\"resultSize\":" + resourceList.size() + "}");
						break;
					} else {
						responseMessage = handler.processMessage();
						streamOut.writeUTF(responseMessage);
						break;
					}
				}
			}
		} catch (IOException ioe) {
			Logger.error(ioe);
		} finally { // Close the conection
			try {
				if (socket != null) {
					socket.close();
					Logger.debug("Socket on thread " + ID + " closed");
				}
				Logger.debug("Server thread " + ID + " closed");
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}
}
