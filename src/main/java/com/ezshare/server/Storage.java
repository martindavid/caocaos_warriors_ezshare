package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Resource;

public class Storage {
	public static ArrayList<Resource> resourceList = new ArrayList<Resource>();
	public static ArrayList<ServerList> serverList = new ArrayList<ServerList>();
	public static String hostName = "";
	public static int port = 0;
	public static String secret = "";
}
