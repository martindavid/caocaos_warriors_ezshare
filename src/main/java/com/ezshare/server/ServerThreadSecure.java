package com.ezshare.server;
//import com.ezshare.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ssl.SSLSocket;

import org.pmw.tinylog.Logger;

import EZShare.Constant;

/**
 * 
 * 
 *         A class to handle thread creation when a new client connected to the
 *         server This class also handle the main logic processing
 *
 */
public class ServerThreadSecure extends Thread {
	
	private String ipAddress;
	private int ID = -1;
	private SSLSocket socket_secure = null;
	private SSLSocket reply_socket = null;

	public ServerThreadSecure(SSLSocket socket, String ipAddress) throws SocketException {
		this.socket_secure = socket;
		this.ipAddress = ipAddress;
		this.ID = socket.getPort();
		this.reply_socket = socket;
	}

	@Override
	public void run() {
		Logger.debug("Server thread " + ID + " running");
		//Create buffered reader to read input from the client
		InputStream inputstream = null;
		try {
			inputstream = socket_secure.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader =new BufferedReader(inputstreamreader);
		
		
		String string = null;
		String jsonString = "unsuccess";
		//Read input from the client and print it to the screen
		try {
			while((string = bufferedreader.readLine()) != null){
				
					jsonString = string;
				
			}
			System.out.println( jsonString);
			Message message = Utilities.convertJsonToObject(jsonString, Message.class);
			
			//Create buffered writer to send data to the client
			OutputStream outputstream = null;
			try {
				outputstream = reply_socket.getOutputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
			BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
			

			if ((message.command.equals(Constant.FETCH.toUpperCase())
					|| message.command.equals(Constant.QUERY.toUpperCase()))
					&& !jsonString.contains("resourceTemplate")) {
				
				bufferedwriter.write(Utilities.getReturnMessage(Constant.MISSING_RESOURCE_TEMPLATE));
				bufferedwriter.flush();
			
			} else if ((message.command.equals(Constant.PUBLISH.toUpperCase())
					|| message.command.equals(Constant.REMOVE.toUpperCase())
					|| message.command.equals(Constant.SHARE.toUpperCase()))
					&& !jsonString.contains("resource")) {
				
				bufferedwriter.write(Utilities.getReturnMessage(Constant.MISSING_RESOURCE));
				bufferedwriter.flush();
				
			} else {
				
				DataOutputStream streamOut = new DataOutputStream(reply_socket.getOutputStream());
				CommandHandler handler = new CommandHandler(message, streamOut, Storage.secret);
				handler.processMessage();
				/*String a = "success";
				System.out.println(a);
				bufferedwriter.write(a);
				bufferedwriter.flush();
				outputstream.close();*/

			}
			Logger.debug(String.format("SERVER: removing %s from ip list", this.ipAddress));
			removeIp(this.ipAddress);
			Logger.debug(String.format("SERVER: ip list size: %d", Storage.ipList.size()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error(e);
		}
		}
	

	private void removeIp(String ipAddress) {
		ConnectionTracking tracking = (ConnectionTracking) Storage.ipList.stream()
				.filter(x -> x.ipAddress.equals(ipAddress)).findAny().orElse(null);
		if (tracking != null) {
			Storage.ipList.remove(tracking);
		}
	}
}
