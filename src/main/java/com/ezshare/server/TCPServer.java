package com.ezshare.server;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by mvalentino on 20/3/17.
 */
public class TCPServer implements Runnable {
	public static ArrayList<ServerList> serverList=new ArrayList<ServerList>();
	
	public Resource[] resources;
	
	private int portNumber;
	private String hostName;
	private ServerThread client;
	private Thread thread;
	private String secret;
	private ServerSocket server = null;
	
	public TCPServer(String hostName, int portNumber, String secret) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.secret = secret;
		try{
			this.server = new ServerSocket(this.portNumber);
		}
		catch(IOException ioe) {
			Logger.error(ioe);
		}
	}
	public void runExchange() throws JsonProcessingException
	{
		//Select a random item from serverList
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(TCPServer.serverList.size());
		ServerList serverListObject=TCPServer.serverList.get(index);
		
		String servHostName=serverListObject.hostname;
		int portNumber=serverListObject.portNumber;
		try {
			if(servHostName.equals(InetAddress.getLocalHost().getHostName()))
			{
				randomGenerator = new Random();
				index = randomGenerator.nextInt(TCPServer.serverList.size());
				ServerList serverListObject1=TCPServer.serverList.get(index);
				
				servHostName=serverListObject1.hostname;
				portNumber=serverListObject1.portNumber;
			}
		} catch (UnknownHostException e2) 
		{
			e2.printStackTrace();
		}
		//Construct Message
		com.ezshare.Message mes;
		mes = new com.ezshare.Message();

		mes.command=Constant.EXCHANGE.toUpperCase();
		com.ezshare.client.Exchange exchange = new com.ezshare.client.Exchange();
		for (ServerList systemList : TCPServer.serverList)
		{
			exchange.hostname = systemList.hostname;
			exchange.port = systemList.portNumber;
			mes.serverList.add(exchange);
		}
		//Create Connection
		try( Socket echoSocket = new Socket(hostName, portNumber);
        		DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());)
		{
			streamOut.writeUTF(mes.toJson());
			String message_echo = "";
			try (
    				DataInputStream streamIn = 
    					new DataInputStream(new BufferedInputStream(echoSocket.getInputStream())))
			{
				while(true) {
					while (streamIn.available() > 0) {
						message_echo = streamIn.readUTF();
						System.out.println(message_echo);
				}
					break;
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				TCPServer.serverList.remove(serverListObject);
				e.printStackTrace();
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			TCPServer.serverList.remove(serverListObject);
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			TCPServer.serverList.remove(serverListObject);
			e1.printStackTrace();
		}
	}
	
	public void run() {
		//int MINUTES = 10; // The delay in minutes
		//Timer timer = new Timer();
		//timer.schedule(task, delay);
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: " + secret);
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
		client = new ServerThread(socket, this.secret);
		client.start();
	}
	
	public void publishResource() throws JsonProcessingException{
		
		Resource res=new Resource();
		res.toJson();
	}
}
