package com.ezshare.server;

import com.ezshare.Resource;

public class Message {
	
	public String command;
	public Resource resource;
	public String secret;
	public boolean relay;
	public Resource resourceTemplate = new Resource();
	public ServerList[] serverList;
}
