package com.ezshare.server;

import org.pmw.tinylog.Logger;

import com.ezshare.server.Exchange;
import com.ezshare.server.model.ConnectionTracking;

import EZShare.Resource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
	private int exchangeInterval;
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

	@Override
	public void run() {
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: " + secret);
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port: " + portNumber);
		Logger.info("Waiting for a client.....");
		Logger.debug("Setting debug on");
		
		// Separate task for server interaction
		this.runExchangeInterval();
		
		// Update global variable
		Storage.hostName = this.hostName;
		Storage.port = this.portNumber;
		Storage.secret = this.secret;

		while (thread != null) {
			try {
				Socket socket = server.accept();
				String ipAddress = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString().replace("/","");;
				Logger.debug(String.format("SERVER: validate %s", ipAddress));
				if (Utilities.isIpAllowed(ipAddress, this.connIntervalLimit)) {
					addThread(socket, ipAddress);
					Logger.debug(String.format("SERVER: add %s to ip list", ipAddress));
					Storage.ipList.add(new ConnectionTracking(ipAddress));
					Logger.debug(String.format("SERVER: ip list size: %d", Storage.ipList.size()));
				}
				
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
	private void addThread(Socket socket, String ipAddress) {
		Logger.debug("Client with IP: " + ipAddress + " connected");
		try {
			client = new ServerThread(socket, ipAddress);
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