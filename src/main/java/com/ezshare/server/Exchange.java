package com.ezshare.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.pmw.tinylog.Logger;

import com.ezshare.server.model.ExchangeMessage;
import com.ezshare.server.model.Server;
import com.fasterxml.jackson.core.JsonProcessingException;

import EZShare.Constant;

public class Exchange {
	private ArrayList<Server> serverList;
	private static final int DEFAULT_TIMEOUT = 5000;

	public Exchange(ArrayList<Server> serverList) {
		this.serverList = serverList;
	}

	public String processCommand(boolean isSecure) throws JsonProcessingException {
		if (this.serverList.size() <= 0) {
			return Utilities.getReturnMessage(Constant.MISSING_OR_INVALID_SERVER_LIST);
		}
		Server existingServer;
		for (Server server : this.serverList) {
			if (isSecure) {
				existingServer = (Server) Storage.secureServerList.stream()
						.filter(x -> x.hostname.equals(server.hostname) && x.port == server.port).findAny()
						.orElse(null);
				if (existingServer == null && isSecureServer(server)) {
					Storage.secureServerList.add(server);
				}
			} else {
				existingServer = (Server) Storage.serverList.stream()
						.filter(x -> x.hostname.equals(server.hostname) && x.port == server.port).findAny()
						.orElse(null);
				if (existingServer == null) {
					Storage.serverList.add(server);
				}
			}
		}
		return Utilities.getReturnMessage(Constant.SUCCESS);
	}

	public void runServerInteraction() {
		int index = (int) (Math.random() * this.serverList.size());
		Server server = this.serverList.get(index);

		try (Socket echoSocket = new Socket(server.hostname, server.port);
				DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());) {
			String message = "";
			try {
				message = new ExchangeMessage(Constant.EXCHANGE.toUpperCase(), this.serverList).toJson();
				Logger.debug(String.format("EXCHANGE: message: %s", message));
			} catch (JsonProcessingException e) {
				Logger.error(e);
			}
			Logger.debug(String.format("EXCHANGE: send message to %s:%d", server.hostname, server.port));
			streamOut.writeUTF(message);

		} catch (UnknownHostException e) {
			Logger.error("Don't know about host " + server.hostname);
			Logger.error(e);
			removeServerFromList(server, false);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + server.hostname);
			Logger.error(e);
			removeServerFromList(server, false);
		}
	}

	public void runSecureServerInteraction() {
		int index = (int) (Math.random() * this.serverList.size());
		Server server = this.serverList.get(index);
		SSLContext sslContext = null;
		try (InputStream keyStoreInput = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(Constant.CLIENT_KEYSTORE_KEY);
				InputStream trustStoreInput = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(Constant.CLIENT_TRUSTSTORE_KEY);) {
			sslContext = Utilities.setSSLFactories(keyStoreInput, Constant.KEYSTORE_PASSWORD, trustStoreInput);
		} catch (Exception e) {
			Logger.error(e);
		}
		// Create SSL socket and connect it to the remote server
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) sslContext.getSocketFactory();
		try (SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(server.hostname, server.port);
				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
				DataInputStream streamIn = new DataInputStream(socket.getInputStream());) {
			String message = "";
			socket.setSoTimeout(DEFAULT_TIMEOUT);
			try {
				message = new ExchangeMessage(Constant.EXCHANGE.toUpperCase(), this.serverList).toJson();
				Logger.debug(String.format("EXCHANGE: message: %s", message));
			} catch (JsonProcessingException e) {
				Logger.error(e);
			}
			Logger.debug(String.format("EXCHANGE: send message to %s:%d", server.hostname, server.port));
			streamOut.writeUTF(message);

			String response = "";
			while (true) {
				if ((response = DataInputStream.readUTF(streamIn)) != null) {
				}
				Logger.debug(response);
				if (response.contains("error")) {
					break;
				}
			}
		} catch (SocketTimeoutException e) {
			Logger.error("[SECURE EXCHANGE] - socket timeout");
			Logger.error(e);
			removeServerFromList(server, true);
		} catch (UnknownHostException e) {
			Logger.error("Don't know about host " + server.hostname);
			Logger.error(e);
			removeServerFromList(server, true);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + server.hostname);
			Logger.error(e);
			removeServerFromList(server, true);
		}
	}

	/**
	 * Helper method to check whether exchange server is secure or not
	 * 
	 * @return true if server is secure, otherwise server is not secure
	 */
	private boolean isSecureServer(Server server) {
		boolean isValid = false;
		SSLContext sslContext = null;
		try (InputStream keyStoreInput = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(Constant.CLIENT_KEYSTORE_KEY);
				InputStream trustStoreInput = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(Constant.CLIENT_TRUSTSTORE_KEY);) {
			sslContext = Utilities.setSSLFactories(keyStoreInput, Constant.KEYSTORE_PASSWORD, trustStoreInput);
		} catch (Exception e) {
			Logger.error(e);
		}
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) sslContext.getSocketFactory();
		try (SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(server.hostname, server.port)) {
			isValid = true; // if there's no exception then server is secure
		} catch (SocketTimeoutException e) {
			Logger.error("[SECURE EXCHANGE] - socket timeout");
			Logger.error(e);
		} catch (UnknownHostException e) {
			Logger.error("Don't know about host " + server.hostname);
			Logger.error(e);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + server.hostname);
			Logger.error(e);
		}
		
		return isValid;
	}

	private void removeServerFromList(Server server, boolean isSecure) {
		Logger.debug(String.format("[EXCHANGE]: removing %s from server list", server.hostname));
		try {
			if (isSecure) {
				Storage.secureServerList.remove(server);
			} else {
				Storage.serverList.remove(server);
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
