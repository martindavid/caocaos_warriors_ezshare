package com.ezshare.server;
//import com.ezshare.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
	private DataInputStream streamIn;
	private DataOutputStream streamOut;
	private String secret;

	public ServerThread(Socket socket, String secret) {
		this.socket = socket;
		this.secret = secret;
		this.ID = socket.getPort();
	}

	public void run() {
		Logger.debug("Server thread " + ID + " running");
		String message = "";
		try {
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			streamOut = new DataOutputStream(socket.getOutputStream());

			while (true) {
				if (streamIn.available() > 0) {
					message = streamIn.readUTF();
					Logger.debug(message);
					Message messageObject = Utilities.convertJsonToObject(message, Message.class);
					CommandHandler handler = new CommandHandler(messageObject, this.secret);
					String responseMessage = "";

					if (messageObject.command.equals(Constant.FETCH.toUpperCase())) {
						if (!message.contains("resourceTemplate")) {
							responseMessage = Utilities.messageReturn(8);
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
								responseMessage = Utilities.messageReturn(7);
							}
							streamOut.writeUTF(responseMessage);
						}
					} else if (messageObject.command.equals(Constant.QUERY.toUpperCase())) {
						Query query = new Query(messageObject.resourceTemplate);
						ArrayList<Resource> resourceList = query.processQuery();

						if (resourceList.size() > 0) {
							String successResponse = new Responses(Constant.SUCCESS, "").toJson();
							streamOut.writeUTF(successResponse);
							for (Resource res : resourceList) {
								streamOut.writeUTF(res.toJson());
							}
							streamOut.writeUTF("{\"resultSize\":" + resourceList.size() + "}");
						} else {
							streamOut.writeUTF(Utilities.messageReturn(7));
						}
					} else {
						responseMessage = handler.processMessage();
						streamOut.writeUTF(responseMessage);
					}
				}
			}
		} catch (IOException ioe) {
			Logger.error(ioe);
		} finally { // Close the conection
			try {
				if (socket != null)
					socket.close();
				if (streamIn != null)
					streamIn.close();
				if (streamOut != null) {
					streamOut.close();
				}
				Logger.debug("Server thread " + ID + " closed");
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}
}
