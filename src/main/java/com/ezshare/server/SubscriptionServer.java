package com.ezshare.server;

import java.io.IOException;
import java.net.Socket;

import org.pmw.tinylog.Logger;

public class SubscriptionServer {
	public String hostName;
	public int port;
	public Socket socket;

	public SubscriptionServer(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		try {
			this.socket = new Socket(hostName, port);
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
