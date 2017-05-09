package com.ezshare.client;


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
import java.io.Writer;
import java.net.UnknownHostException;

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
	SSLSocket receive_socket;
	SSLSocketFactory sslsocketfactory;

	public SSLClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
	}

	public void Execute() throws IOException {
		
			
			//Location of the Java keystore file containing the collection of
			//certificates trusted by this application(trust store).
			System.setProperty("javax.net.ssl.trustStore", "clientKeyStore/clientKeystore.jks");
			
			System.setProperty("javax.net.debug", "all");
			
			//Create SSL socket and connect it to the remote server
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
			
			
			message_transfer();
			
			message_receive();
		
		
	}
	
	public void message_transfer() throws IOException{
		
		//Create buffered writer to send data to the server
		OutputStream outputstream = echoSocket.getOutputStream();
		OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
		BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
		
		//Send data to the server
		bufferedwriter.write(message.toJson());
		bufferedwriter.flush();
		echoSocket.close();
		
	}
	
	public void message_receive() throws IOException {
		//Create buffered reader to read input from the server
		receive_socket = (SSLSocket) sslsocketfactory.createSocket(hostName, portNumber);
		System.out.println("1");
		InputStream inputstream = receive_socket.getInputStream();
		System.out.println("2");
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		System.out.println("3");
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		System.out.println("4");

		String response = "";
		try {
			String string = null;
			String jsonString = "";
			System.out.println("5");
			//Read input from the server and print it to the screen
			try {
				System.out.println("6");
				while((string = bufferedreader.readLine()) != ""){
					jsonString = string;
					System.out.println("7");
				}
				System.out.println(jsonString);
				
			boolean exitLoop = false;
			if (message.command.equals(Constant.FETCH.toUpperCase())) {
				while (true) {
					if ((string = bufferedreader.readLine()) != null) {
						// Receive response from server (success or error)
						
						response = string;
						
						Logger.info(response);
						Responses serverResponse = Utilities.convertJsonToObject(response, Responses.class);

						// Only fetch the file the response is not an error
						if (!serverResponse.response.equals(Constant.ERROR)) {
							DataInputStream streamIn = new DataInputStream(new BufferedInputStream(receive_socket.getInputStream()));
							// Receiving the file
							fileTransfer = new FileTransfer(streamIn);
							fileTransfer.download();
						}
						exitLoop = true;
					}
					if (exitLoop) {
						
					}
				}
			} else {
				while (true) {
					while ((string = bufferedreader.readLine()) != null) {
						response = string;
						Logger.info(response);
						if (!message.command.equals(Constant.QUERY.toUpperCase())
								|| response.contains(Constant.RESULT_SIZE)) {
							
						}
						if (response.contains("error")) {
							
						}
					}
				}
			}
		} catch (IOException ioe) {
			Logger.error(ioe);
		}
	} catch (Exception e) {
		Logger.error(e);
	}
	}

}
