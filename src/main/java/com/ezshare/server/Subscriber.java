package com.ezshare.server;

import java.io.DataOutputStream;
import java.net.Socket;

import org.pmw.tinylog.Logger;

import EZShare.Resource;

public class Subscriber {
	public String id;
	public int resultSize;
	public Resource subscribeTemplate;
	public Socket subscriberSocket;
	public DataOutputStream streamOut;

	public Subscriber(String id, int resultSize, Resource subscribeTemplate, Socket subscriberSocket) {
		this.id = id;
		this.resultSize = resultSize;
		this.subscribeTemplate = subscribeTemplate;
		this.subscriberSocket = subscriberSocket;
		try {
			this.streamOut = new DataOutputStream(subscriberSocket.getOutputStream());
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
