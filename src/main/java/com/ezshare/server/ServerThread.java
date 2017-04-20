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
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut;
	private String secret;

	public ServerThread(Socket socket, String secret) {
		this.socket = socket;
		this.secret = secret;
		this.ID = socket.getPort();
	}

	public void run() {
		Logger.info("Server thread " + ID + " running");
		String message = "";
		try {
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			streamOut = new DataOutputStream(socket.getOutputStream());
			while(true) {
				if (streamIn.available() > 0) {
					message = streamIn.readUTF();
					Message messageObject = Utilities.convertJsonToObject(message, Message.class);
					String responseMessage = "";
					if (messageObject.command.equals(Constant.PUBLISH.toUpperCase())) {
						Publish publish = new Publish(messageObject.resource);
						responseMessage = publish.processResourceMessage();

					}else if (messageObject.command.equals(Constant.SHARE.toUpperCase())){
						ShareCommand sharec = new ShareCommand(messageObject.resource,messageObject.secret,this.secret);
						responseMessage = sharec.processResourceMessage();
					}
					else if (messageObject.command.equals(Constant.REMOVE.toUpperCase())) {
						RemoveCommand remove = new RemoveCommand(messageObject.resource);
						responseMessage = remove.processResource();
					} else if (messageObject.command.equals(Constant.EXCHANGE.toUpperCase())) {
					    ExchangeCommand exchange = new ExchangeCommand(messageObject.serverList);
					    responseMessage = exchange.processCommand();
					} else if (messageObject.command.equals(Constant.QUERY.toUpperCase())) {
						QueryCommand query = new QueryCommand(messageObject.resourceTemplate);
						// Process the Query
						QueryResponse qresponse = query.processQuery();
						// Variable to store the List answer
						ArrayList<Resource> responseList = qresponse.getResultList();
						// Get the response message
						String resMess = qresponse.getResponseMessage();
						// Check to append the resources
						if (responseList.size() != 0) {
							for (Resource resourceIterator : responseList) {
								resMess = resMess + "\n" + resourceIterator.toJson();
							}
							Responses resp = new Responses();
							resp.response = "error";
							int size=responseList.size();
							resp.resultSize = Integer.toString(size);
							responseMessage = resMess+resp.toJson();
						} 
						else {
							responseMessage = resMess;
						}

					}
					else if (messageObject.command.equals(Constant.FETCH.toUpperCase()))
					{
						if(!message.contains("resourceTemplate")){
							responseMessage = Utilities.messageReturn(8);
						} else {
							Fetch fetch = new Fetch(messageObject.resourceTemplate);
							FetchResponse fetchsponse = fetch.proceFetch();
							if(messageObject.resourceTemplate.uri != "" && fetchsponse != null){
								String resMess = fetchsponse.getResponseMessage();
								streamOut.writeUTF(resMess);
								Resource resp = fetchsponse.getResource();
								if (resp != null){
									FileTransfer file = new FileTransfer(streamIn, streamOut, messageObject.resourceTemplate.uri);
									resp.resourceSize = file.getFileSize();
									streamOut.writeUTF(resp.toJson());
									file.send();
								}
								responseMessage = fetchsponse.adsize.toJson();
							}else{
								responseMessage = Utilities.messageReturn(7);
							}
						}
					}
					//Taking into account cases where command is not found
					else
					{
						responseMessage=Utilities.messageReturn(6);
					}
					
					streamOut.writeUTF(responseMessage);
					
					System.out.println(message);
				}	
			}

		} catch (IOException ioe) {
			Logger.error(ioe);
		}
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
	}
}
