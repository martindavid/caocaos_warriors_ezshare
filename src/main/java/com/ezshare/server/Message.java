package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
	
	public String command;
	public Resource resource;
	public String secret;
	public boolean relay;
	public Resource resourceTemplate = new Resource();
	public ArrayList<ServerList> serverList= new ArrayList<ServerList>();
	
	

}
