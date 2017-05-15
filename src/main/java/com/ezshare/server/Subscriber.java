package com.ezshare.server;

import java.net.Socket;

import EZShare.Resource;

public class Subscriber {
	public String id;
	public int resultSize;
	public Resource subscribeTemplate;
	public Socket subscriberSocket;
	
	public Subscriber(String id, int resultSize, Resource subscribeTemplate, Socket subscriberSocket) {
		this.id = id;
		this.resultSize = resultSize;
		this.subscribeTemplate = subscribeTemplate;
		this.subscriberSocket = subscriberSocket;
	}
}
