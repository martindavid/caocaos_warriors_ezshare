package com.ezshare.server;

import java.util.ArrayList;
import EZShare.Resource;

public class Storage {
	public static ArrayList<Resource> resourceList = new ArrayList<Resource>();
	public static ArrayList<Server> serverList = new ArrayList<Server>();
	public static ArrayList<Server> secureServerList = new ArrayList<Server>();
	public static ArrayList<ConnectionTracking> ipList = new ArrayList<ConnectionTracking>();

	public static String hostName = "";
	public static int port = 0;
	public static String secret = "";

	public static ArrayList<Subscriber> subscriber = new ArrayList<Subscriber>();
	public static ArrayList<SecureSubscriber> secureSubscriber = new ArrayList<SecureSubscriber>();
	public static ArrayList<SubscriptionServerThread> serverThread = new ArrayList<SubscriptionServerThread>();
	public static ArrayList<SubscriptionResources> subscriptionResources = new ArrayList<SubscriptionResources>();
}
