package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Resource;

public class Message {
	public String command;
	public Resource resource;
	public String secret;
	public boolean relay;
	public Resource resourceTemplate = new Resource();
	public ArrayList<ServerList> serverList = new ArrayList<ServerList>();
}
