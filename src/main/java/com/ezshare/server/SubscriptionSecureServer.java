package com.ezshare.server;

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.pmw.tinylog.Logger;

import EZShare.Constant;

public class SubscriptionSecureServer {
	public String hostName;
	public int port;
	public SSLSocket socket;

	public SubscriptionSecureServer(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;

		System.setProperty(Constant.JAVANET_TRUSTSTORE_PROP, Constant.CERTIFICATE_KEY);
		System.setProperty(Constant.JAVANET_KEYSTOREPASS_PROP, Constant.CERTIFICATE_PASSWORD);
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		try {
			this.socket = (SSLSocket) sslsocketfactory.createSocket(hostName, port);
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
