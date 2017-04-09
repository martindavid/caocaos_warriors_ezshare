package com.ezshare.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.pmw.tinylog.Logger;

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
					System.out.println(message);}}
			
			
			
//			streamOut = new DataOutputStream(socket.getOutputStream());
//			streamOut.writeUTF(message);
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
