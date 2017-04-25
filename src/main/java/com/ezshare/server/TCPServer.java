package com.ezshare.server;

import org.pmw.tinylog.Logger;



import com.ezshare.Constant;
import com.ezshare.Resource;
import com.ezshare.client.Exchange;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mvalentino on 20/3/17.
 */
public class TCPServer implements Runnable {
	public Resource[] resources;
	


	private int portNumber;
	private String hostName;
	private ServerThread client;
	private Thread thread;
	private String secret;
	private ServerSocket server = null;
	private static int exchangeInterval=10;
	private int connIntervalLimit;
	private ExecutorService es;
	

	public TCPServer(String hostName, int portNumber, String secret, int exchangeInterval, int connIntervalLimit) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.secret = secret;
		this.exchangeInterval = exchangeInterval;
		this.connIntervalLimit = connIntervalLimit;
		this.es = Executors.newCachedThreadPool();

		
		
		try {
			this.server = new ServerSocket(this.portNumber);
		} catch (IOException ioe) {
			Logger.error(ioe);
		}
	}

	public void runExchange() throws JsonProcessingException {
		// Select a random item from serverList
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(Storage.serverList.size());
		ServerList serverListObject = Storage.serverList.get(index);

		String servHostName = serverListObject.hostname;
		int portNumber = serverListObject.port;
		try {
			if (servHostName.equals(InetAddress.getLocalHost().getHostName())) {
				randomGenerator = new Random();
				index = randomGenerator.nextInt(Storage.serverList.size());
				ServerList serverListObject1 = Storage.serverList.get(index);

				servHostName = serverListObject1.hostname;
				portNumber = serverListObject1.port;
			}
		} catch (UnknownHostException e) {
			Logger.error(e);
		}
		// Construct Message
		com.ezshare.Message mes;
		mes = new com.ezshare.Message();

		mes.command = Constant.EXCHANGE.toUpperCase();
		String hostName = "";
		int port = 0;
		for (ServerList systemList : Storage.serverList) {
			hostName = systemList.hostname;
			port = systemList.port;
			mes.serverList.add(new Exchange(hostName, port));
		}
		// Create Connection
		try (Socket echoSocket = new Socket(hostName, portNumber);
				DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());) {
			streamOut.writeUTF(mes.toJson());
			String message_echo = "";
			try (DataInputStream streamIn = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()))) {
				while (true) {
					while (streamIn.available() > 0) {
						message_echo = streamIn.readUTF();
						Logger.debug(message_echo);
					}
					break;
				}
			} catch (IOException e) {
				Storage.serverList.remove(serverListObject);
				Logger.error(e);
			}
		} catch (UnknownHostException e1) {
			Storage.serverList.remove(serverListObject);
			e1.printStackTrace();
		} catch (IOException e1) {
			Storage.serverList.remove(serverListObject);
			e1.printStackTrace();
		}
	}

	public void run() {
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: " + secret);
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port: " + portNumber);
		Logger.info("Waiting for a client.....");
		
		Logger.debug("Setting debug on");
		this.startTimer();
		// Update global variable
		Storage.hostName = this.hostName;
		Storage.port = this.portNumber;
		Storage.secret = this.secret;
		


		
		while (thread != null) {
			try {
				addThread(server.accept());
			} catch (IOException e) {
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
	 * 
	 * @param socket
	 */
	public void addThread(Socket socket) {
		Logger.debug("Client connected: " + socket);
		Logger.debug("Client with IP: " + socket.getInetAddress() + " connected");
		try {
			client = new ServerThread(socket, this.connIntervalLimit);
			this.es.execute(client);
		} catch (SocketException e) {
			Logger.error(e);
		}
		
	}

    public static void startTimer(){
        TimerTask task = new TimerTask() {

			@Override
			public void run() {
				this.runServerInteraction();
				
			}

			private void runServerInteraction() {
				if(Storage.serverList.size()>0)
				{
					int index = (int)(Math.random()*Storage.serverList.size());
					Logger.info(index);
					try {
						this.ServerExchange(Storage.serverList.get(index),index);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}

			private void ServerExchange(ServerList server,int index) throws UnknownHostException, IOException {
				ExchangeMessage message = new ExchangeMessage();
				String mes="";
				message.command = "EXCHANGE";
				message.serverList = Storage.serverList;
				mes = message.toJson();
				
				try(Socket echoSocket = new Socket(server.hostname, server.port);
				DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());){
				streamOut.writeUTF(mes);
				Logger.debug(mes);}
				catch(UnknownHostException e)
				{
					Storage.serverList.remove(index);
					Logger.error("Don't know about host " + server.hostname);
					Logger.error(e);
				}
				catch (IOException e) {
					Logger.error("Couldn't get I/O for the connection to " + server.hostname);
					Logger.error(e);
					Storage.serverList.remove(index);
				}
			}
			
			
			
           
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000*5,1000*exchangeInterval*60);
    }
	
}



