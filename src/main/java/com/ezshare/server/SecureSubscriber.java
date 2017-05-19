package com.ezshare.server;

import javax.net.ssl.SSLSocket;

import EZShare.Resource;

public class SecureSubscriber {
	public String id;
	public int resultSize;
	public Resource subscribeTemplate;
	public SSLSocket subscriberSocket;

	public SecureSubscriber(String id, int resultSize, Resource subscribeTemplate, SSLSocket subscriberSocket) {
		this.id = id;
		this.resultSize = resultSize;
		this.subscribeTemplate = subscribeTemplate;
		this.subscriberSocket = subscriberSocket;
	}
}
