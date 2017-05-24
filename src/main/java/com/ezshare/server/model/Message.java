package com.ezshare.server.model;

import java.util.ArrayList;

import EZShare.Resource;

public class Message {
	public String command;
	public Resource resource;
	public String secret;
	public boolean relay;
	public Resource resourceTemplate = new Resource();
	public ArrayList<Server> serverList = new ArrayList<Server>();
	public String id;
}
