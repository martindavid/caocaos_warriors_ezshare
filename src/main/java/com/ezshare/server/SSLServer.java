package com.ezshare.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.pmw.tinylog.Logger;

import com.ezshare.server.Exchange;
import com.ezshare.server.SecureServerThread;
import com.ezshare.server.Storage;
import com.ezshare.server.model.ConnectionTracking;

import EZShare.Constant;
import EZShare.Resource;

public class SSLServer implements Runnable {
	public Resource[] resources;

	private int portNumber;
	private String hostName;
	private SecureServerThread client;
	private Thread thread;
	private String secret;
	private int exchangeInterval;
	private int connIntervalLimit;
	private ExecutorService es;
	private SSLServerSocket sslserversocket;
	private SSLSocket socket;
	private SSLContext sslContext;

	public SSLServer(String hostName, int portNumber, String secret, int exchangeInterval, int connIntervalLimit) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.secret = secret;
		this.exchangeInterval = exchangeInterval;
		this.connIntervalLimit = connIntervalLimit;
		this.es = Executors.newCachedThreadPool();
		try (InputStream keyStoreInput = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(Constant.SERVER_KEYSTORE_KEY);
				InputStream trustStoreInput = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(Constant.SERVER_TRUSTSTORE_KEY);) {
			this.sslContext = Utilities.setSSLFactories(keyStoreInput, Constant.KEYSTORE_PASSWORD, trustStoreInput);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	@Override
	public void run() {
		Logger.info("Starting the EZShare Secure Server");
		Logger.info("bound to port: " + portNumber);

		// Create SSL server socket
		SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) this.sslContext.getServerSocketFactory();
		try {
			sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(portNumber);
		} catch (IOException e1) {
			Logger.error(e1);
		}

		// Separate task for server interaction
		this.runExchangeInterval();

		// Update global variable
		Storage.hostName = this.hostName;
		Storage.port = this.portNumber;
		Storage.secret = this.secret;

		while (thread != null) {
			// Accept client connection
			try {
				socket = (SSLSocket) sslserversocket.accept();
			} catch (IOException e) {
				Logger.error(e);
			}
			String ipAddress = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString()
					.replace("/", "");
			;
			Logger.debug(String.format("SERVER: validate %s", ipAddress));
			if (Utilities.isIpAllowed(ipAddress, this.connIntervalLimit)) {
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
	 * Create a new thread every time the client connected
	 * 
	 * @param socket
	 */
	private void addThread(SSLSocket socket, String ipAddress) {
		Logger.debug("Client with IP: " + ipAddress + " connected");
		try {
			client = new SecureServerThread(socket, ipAddress);
			this.es.execute(client);
		} catch (SocketException e) {
			Logger.error(e);
		}

	}

	private void runExchangeInterval() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Logger.debug("START - secure server interaction");
				if (Storage.serverList.size() > 0) {
					Exchange exchange = new Exchange(Storage.secureServerList);
					exchange.runSecureServerInteraction();
				}
				Logger.debug("END - secure server interaction");
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 1000 * 5, 1000 * exchangeInterval);
	}
}
