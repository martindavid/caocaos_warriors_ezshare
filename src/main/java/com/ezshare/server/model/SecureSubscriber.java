package com.ezshare.server.model;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.net.ssl.SSLSocket;

import org.pmw.tinylog.Logger;

import EZShare.Resource;

public class SecureSubscriber {
	public String id;
	public int resultSize;
	public Resource subscribeTemplate;
	public SSLSocket subscriberSocket;
	public DataOutputStream streamOut;

	public SecureSubscriber(String id, int resultSize, Resource subscribeTemplate, SSLSocket subscriberSocket) {
		this.id = id;
		this.resultSize = resultSize;
		this.subscribeTemplate = subscribeTemplate;
		this.subscriberSocket = subscriberSocket;
		try {
			this.streamOut = new DataOutputStream(subscriberSocket.getOutputStream());
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
