package com.ezshare.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.FileTransfer;
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
	
	public ServerThread(Socket socket) {
		this.socket = socket;
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
					Message messageObject = Utilities.toMessageObject(message);
					String responseMessage = "";
					if (messageObject.command.equals(Constant.PUBLISH.toUpperCase())) {
						// do sth
						Publish publish = new Publish(messageObject.resource);
						responseMessage = publish.processResourceMessage();

					} else if (messageObject.command.equals(Constant.REMOVE.toUpperCase())) {
						RemoveCommand remove = new RemoveCommand(messageObject.resource);
						responseMessage = remove.processResource();
					} else if (messageObject.command.equals(Constant.QUERY.toUpperCase())) {
						QueryCommand query = new QueryCommand(messageObject.resource);
						// Process the Query
						QueryResponse qresponse = query.processQuery();
						// Variable to store the List answer
						ArrayList<Resource> responseList = qresponse.getResultList();
						// Get the response message
						String resMess = qresponse.getResponseMessage();
						// Check to append the resources
						if (responseList.size() != 0) {
							for (Resource resourceIterator : Resource.resourceList) {
								resMess = resMess + "\n" + resourceIterator.toJson();
							}
							responseMessage = resMess;
						} else {
							responseMessage = resMess;
						}

					}
					else if (messageObject.command.equals(Constant.FETCH.toUpperCase()))
					{
						
						if(!message.contains("resourceTemplate")){
							responseMessage = Utilities.messageReturn(8);
						} else {
							Fetch fetch = new Fetch(messageObject.resource);
							FetchResponse fetchsponse = fetch.proceFetch();
							if(messageObject.resourceTemplate.uri.contains("file") && fetchsponse != null){
								String resMess = fetchsponse.getResponseMessage();
								streamOut.writeUTF(resMess);
								if(fetchsponse.res != null){
									Resource resp = fetchsponse.getResource();
									FileTransfer file = new FileTransfer(socket, messageObject.resource.uri);
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
