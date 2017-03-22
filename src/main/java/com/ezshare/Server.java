package com.ezshare;

import org.pmw.tinylog.Logger;

import com.ezshare.server.TCPServer;

public class Server {
	
	public static void main(String[] args) {
		Logger.info("Server is running");
		TCPServer server = new TCPServer();
		server.Execute();
	}
}
