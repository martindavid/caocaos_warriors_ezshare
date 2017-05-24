package com.ezshare.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.pmw.tinylog.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import EZShare.Constant;

public class Exchange {
	private ArrayList<Server> serverList;

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
				if (existingServer == null) {
					Storage.secureServerList.add(server);
					//Storage.subscriptionSecureServer.add(new SubscriptionSecureServer(server.hostname, server.port));
				}
			} else {
				existingServer = (Server) Storage.serverList.stream()
						.filter(x -> x.hostname.equals(server.hostname) && x.port == server.port).findAny()
						.orElse(null);
				if (existingServer == null) {
					Storage.serverList.add(server);
					//Storage.subscriptionServer.add(new SubscriptionServer(server.hostname, server.port));
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
			Logger.debug(String.format("EXCHANGE: removing %s from server list", server.hostname));
			Storage.serverList.remove(index);
			Logger.error(e);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + server.hostname);
			Logger.debug(String.format("EXCHANGE: removing %s from server list", server.hostname));
			Storage.serverList.remove(index);
			Logger.error(e);
		}
	}

	public void runSecureServerInteraction() {
		int index = (int) (Math.random() * this.serverList.size());
		Server server = this.serverList.get(index);
		System.setProperty("javax.net.ssl.trustStore", "clientKeyStore/clientKeystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "comp90015");
		// Create SSL socket and connect it to the remote server
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		try (SSLSocket echoSocket = (SSLSocket) sslsocketfactory.createSocket(server.hostname, server.port);
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
			Logger.debug(String.format("EXCHANGE: removing %s from server list", server.hostname));
			Storage.serverList.remove(index);
			Logger.error(e);
		} catch (IOException e) {
			Logger.error("Couldn't get I/O for the connection to " + server.hostname);
			Logger.debug(String.format("EXCHANGE: removing %s from server list", server.hostname));
			Storage.serverList.remove(index);
			Logger.error(e);
		}
	}
}
