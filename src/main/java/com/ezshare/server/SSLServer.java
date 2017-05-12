package com.ezshare.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.pmw.tinylog.Logger;

import com.ezshare.server.ConnectionTracking;
import com.ezshare.server.Exchange;
import com.ezshare.server.ServerThreadSecure;
import com.ezshare.server.Storage;

import EZShare.Resource;

public class SSLServer implements Runnable {
	public Resource[] resources;

	private int portNumber;
	private String hostName;
	private ServerThreadSecure client;
	private Thread thread;
	private String secret;
	private int exchangeInterval;
	private int connIntervalLimit;
	private ExecutorService es;
	private SSLServerSocket sslserversocket;
	private SSLSocket socket;

	public SSLServer(String hostName, int portNumber, String secret, int exchangeInterval, int connIntervalLimit) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.secret = secret;
		this.exchangeInterval = exchangeInterval;
		this.connIntervalLimit = connIntervalLimit;
		this.es = Executors.newCachedThreadPool();
	}

	public void run() {
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: " + secret);
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port: " + portNumber);
		Logger.info("Waiting for a client.....");

		Logger.debug("Setting debug on");
		
		// Specify the keystore details (this can be specified as VM arguments as well)
		// the keystore file contains an application's own certificate and private key
		System.setProperty("javax.net.ssl.keyStore", "serverKeystore/serverKeystore.jks");
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword", "comp90015");
										
		//Enable debugging to view the handshake and communication which happens between the server and client
		System.setProperty("javax.net.debug", "all");
				
		//Create SSL server socket
		SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try {
			sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(portNumber);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Separate task for server interaction
		this.runExchangeInterval();
		
		// Update global variable
		Storage.hostName = this.hostName;
		Storage.port = this.portNumber;
		Storage.secret = this.secret;
		


		while (thread != null) {
			//Accept client connection
			try {
				socket = (SSLSocket) sslserversocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String ipAddress = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString().replace("/","");;
			Logger.debug(String.format("SERVER: validate %s", ipAddress));
			if (isIpAllowed(ipAddress)) {
				addThread(socket, ipAddress);
				Logger.debug(String.format("SERVER: add %s to ip list", ipAddress));
				Storage.ipList.add(new ConnectionTracking(ipAddress));
				Logger.debug(String.format("SERVER: ip list size: %d", Storage.ipList.size()));
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
	 * Validate whether incoming IP address allowed to make a connection or not
	 * We validate against IP list array that system maintains
	 * @param ipAddress
	 * @return
	 */
	private boolean isIpAllowed(String ipAddress) {
		ConnectionTracking tracking = (ConnectionTracking) Storage.ipList.stream().filter(x -> x.ipAddress.equals(ipAddress))
										.findAny()
										.orElse(null);
		if (tracking != null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date now = new Date();
			try {
				Date lastConnected = dateFormat.parse(tracking.timeStamp);
				if ((now.getTime() - lastConnected.getTime())/ 1000 % 60 <= this.connIntervalLimit) {
					return false;
				}
			} catch (ParseException e) {
				Logger.error(e);
			}
		}
		
		return true;
	}

	/**
	 * Create a new thread every time the client connected
	 * 
	 * @param socket
	 */
	private void addThread(SSLSocket socket, String ipAddress) {
		Logger.debug("Client with IP: " + ipAddress + " connected");
		try {
			client = new ServerThreadSecure(socket, ipAddress);
			this.es.execute(client);
		} catch (SocketException e) {
			Logger.error(e);
		}

	}

	private void runExchangeInterval() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Logger.debug("START - server interaction");
				if (Storage.serverList.size() > 0) {
					Exchange exchange = new Exchange(Storage.serverList);
					exchange.runServerInteraction();
				}
				Logger.debug("END - server interaction");
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, 1000 * 5, 1000 * exchangeInterval);
	}
}
