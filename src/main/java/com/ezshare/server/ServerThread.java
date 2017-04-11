package com.ezshare.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.FileTransfer;
/**
 * @author mvalentino
 * 
 * A class to handle thread creation when a new client connected to the server
 * This class also handle the main logic processing
 *
 */
public class ServerThread extends Thread {
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut;
	private FileTransfer filerecive;
	
	public ServerThread(Socket socket) {
		this.socket = socket;
		this.ID = socket.getPort();
	}
	
	public void run() {
		Logger.info("Server thread " + ID + " running");
		String message = "";
		try{
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			while(true) {
				if (streamIn.available() > 0) {
					message = streamIn.readUTF();
					Message messageObject=Utilities.toMessageObject(message);
					String responseMessage="";
					if (messageObject.command.equals(Constant.PUBLISH.toUpperCase()))
					{
						//do sth
						PublishCommand publish=new PublishCommand(messageObject.resource);
						responseMessage=publish.processResourceMessage();
						
					}
					else if (messageObject.command.equals(Constant.REMOVE.toUpperCase()))
					{
						RemoveCommand remove=new RemoveCommand(messageObject.resource);
						responseMessage=remove.processResource();
					}
					//Taking into account cases where command is not found
					else
					{
						responseMessage=Utilities.messageReturn(6);
					}
					streamOut = new DataOutputStream(socket.getOutputStream());
					streamOut.writeUTF(responseMessage);
					
					System.out.println(message);
				}	
			}
			
                }
		catch (IOException ioe) {
			// TODO: handle exception
			Logger.error(ioe);
		}
	}
	
	public void close() throws IOException {
		if (socket != null) socket.close();
		if (streamIn != null) streamIn.close();
	}
}
