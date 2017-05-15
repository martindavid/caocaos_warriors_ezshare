package com.ezshare.server;

import java.util.ArrayList;

import EZShare.Resource;

public class Storage {
	public static ArrayList<Resource> resourceList = new ArrayList<Resource>();
	public static ArrayList<ServerList> serverList = new ArrayList<ServerList>();
	public static ArrayList<ServerList> secure_serverList = new ArrayList<ServerList>();
	public static ArrayList<ConnectionTracking> ipList = new ArrayList<ConnectionTracking>();
	public static String hostName = "";
	public static int port = 0;
	public static String secret = "";
}
