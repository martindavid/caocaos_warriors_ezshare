package com.ezshare.server;
//import com.ezshare.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;

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

	public ServerThread(Socket socket, int connIntervalLimit) throws SocketException {
		this.socket = socket;
		this.socket.setSoTimeout(connIntervalLimit * 1000);
		this.ID = socket.getPort();
	}

	@Override
	public void run() {
		Logger.debug("Server thread " + ID + " running");
		String jsonString = "";
		try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());) {
			while (true) {
				if (streamIn.available() > 0) {
					jsonString = streamIn.readUTF();
					Logger.debug(jsonString);
					Message message = Utilities.convertJsonToObject(jsonString, Message.class);

					if ((message.command.equals(Constant.FETCH.toUpperCase())
							|| message.command.equals(Constant.QUERY.toUpperCase()))
							&& !jsonString.contains("resourceTemplate")) {
						streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE_TEMPLATE));
						break;
					} else if ((message.command.equals(Constant.PUBLISH.toUpperCase())
							|| message.command.equals(Constant.REMOVE.toUpperCase())
							|| message.command.equals(Constant.SHARE.toUpperCase()))
							&& !jsonString.contains("resource")) {
						streamOut.writeUTF(Utilities.getReturnMessage(Constant.MISSING_RESOURCE));
					} else {
						CommandHandler handler = new CommandHandler(message, streamOut, Storage.secret);
						handler.processMessage();
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
