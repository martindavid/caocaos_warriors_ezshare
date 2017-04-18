package com.ezshare.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.ezshare.Message;
import com.ezshare.server.Responses;

/**
 * Created by mvalentino on 20/3/17.
 * 
 * Class that handles client message construction and send it to the server
 */
public class TCPClient {

	private Message message;
	private int portNumber;
	private String hostName;
	private FileTransfer fileTransfer;

	public TCPClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
	}

	public void Execute() throws IOException {
		try (Socket echoSocket = new Socket(hostName, portNumber);
				DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());) {
			// Print all of the information
			Logger.info("Starting the EZShare Server");
			Logger.info("using secret: "); // TODO: create functionality to
											// generate secret
			Logger.info("using advertised hostname: " + hostName);
			Logger.info("bound to port " + portNumber);
			Logger.info("started");

			// Log it first and send to the server
			Logger.debug("Setting Debug On");
			Logger.debug("[SENT]:" + message.toJson());
			streamOut.writeUTF(message.toJson());

			String response = "";
			try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()))) {
			    int exitCounter = 0;
				if (message.command.equals("FETCH")) {
					while (true) {
						if (streamIn.available() > 0) {
							// Receive 
							response = streamIn.readUTF();
							Logger.info(response);
							Responses serverResponse = Responses.fromJson(response);
							
							// Only fetch the file the response is not an error
							if (!serverResponse.response.equals("error")) {
								fileTransfer = new FileTransfer(echoSocket, message.resourceTemplate.uri);
								fileTransfer.receive();
								response = streamIn.readUTF();
								Logger.info(response);
							}
							exitCounter = 3;
						}
						if (exitCounter == 3) { break; }
					}
				} else {
					while (true) {
						if (streamIn.available() > 0) {
							response = streamIn.readUTF();
							Logger.info(response);
							if (!message.command.equals("QUERY") || response.contains("resultSize")) {
							    break;
							}
						}
					}
				}
			}
			catch (IOException ioe) {
				Logger.error(ioe);
			}
		} catch (UnknownHostException e) {
			Logger.error("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		} catch (Exception e) {
			Logger.error(e);
		}

	}
}
