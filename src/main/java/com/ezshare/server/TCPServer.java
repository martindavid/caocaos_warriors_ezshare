package com.ezshare.server;

import org.pmw.tinylog.Logger;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mvalentino on 20/3/17.
 */
public class TCPServer implements Runnable {
	public Resource[] resources;
	
	private int portNumber;
	private String hostName;
	private ServerThread client;
	private Thread thread;
	private ServerSocket server = null;
	
	public TCPServer(String hostName, int portNumber) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		try{
			this.server = new ServerSocket(this.portNumber);
		}
		catch(IOException ioe) {
			Logger.error(ioe);
		}
	}
	
	public void run() {
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: "); // TODO: put a secret
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port: " + portNumber);
		Logger.info("Waiting for a client.....");
		
		
		while (thread != null) {
			try {
				addThread(server.accept());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.error(e);
			}
		}
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop() {
		if (thread != null) {
			thread = null;
		}
	}
	
	/**
	 * Create a new thread every time the client connected
	 * @param socket
	 */
	public void addThread(Socket socket) {
		Logger.info("Client connected: " + socket);
		client = new ServerThread(socket);
		client.start();
	}
	
	public void publishResource() throws JsonProcessingException{
		
		Resource res=new Resource();
		res.toJson();
	}
}
