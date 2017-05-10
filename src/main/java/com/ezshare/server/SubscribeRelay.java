package com.ezshare.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import EZShare.Constant;
import EZShare.Message;
import EZShare.Resource;

public class SubscribeRelay {

	String hostName;
	int portNumber;
	Message message;

	public SubscribeRelay(String hostName, int portNumber, Message message) {
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.message = message;
	}

	public ArrayList<Resource> fetchResourceList() {
		ArrayList<Resource> result = new ArrayList<Resource>();
		try (Socket echoSocket = new Socket(hostName, portNumber);
				DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());) {
			echoSocket.setSoTimeout(10 * 1000);
			streamOut.writeUTF(message.toJson());

			String response = "";
			try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()))) {
				while (true) {
					if (streamIn.available() > 0) {
						response = streamIn.readUTF();
						if (response.contains(Constant.RESULT_SIZE)) {
							break;
						}
						if (!response.contains(Constant.SUCCESS)) {
							Resource res = Utilities.convertJsonToObject(response, Resource.class);
							if (!res.owner.isEmpty() && res.owner != "*") {
								res.owner = "*";
							}
							res.ezserver = String.format("%s:%d", hostName, portNumber);
							result.add(res);
						}
					}
				}
			} catch (IOException ioe) {
				Logger.error(ioe);
			}
		} catch (UnknownHostException e) {
			Logger.error("Don't know about host " + hostName);
			Logger.error(e);
		} catch (SocketException e) {
			Logger.error("Request time out");
			Logger.error(e);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + hostName);
			Logger.error(e);
		} catch (Exception e) {
			Logger.error(e);
		}

		return result;
	}
}

