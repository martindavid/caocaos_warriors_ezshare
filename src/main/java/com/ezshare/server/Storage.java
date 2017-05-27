package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.server.model.ConnectionTracking;
import com.ezshare.server.model.SecureSubscriber;
import com.ezshare.server.model.Server;
import com.ezshare.server.model.Subscriber;
import com.ezshare.server.model.SubscriptionResources;

import EZShare.Resource;

public class Storage {
	public static ArrayList<Resource> resourceList = new ArrayList<Resource>();
	public static ArrayList<Resource> secureResourceList = new ArrayList<Resource>();
	public static ArrayList<Server> serverList = new ArrayList<Server>();
	public static ArrayList<Server> secureServerList = new ArrayList<Server>();
	public static ArrayList<ConnectionTracking> ipList = new ArrayList<ConnectionTracking>();

	public static String hostName = "";
	public static int port = 0;
	public static String secret = "";

	public static ArrayList<Subscriber> subscriber = new ArrayList<Subscriber>();
	public static ArrayList<SecureSubscriber> secureSubscriber = new ArrayList<SecureSubscriber>();
	public static ArrayList<SubscriptionServerThread> subscriptionServerThread = new ArrayList<SubscriptionServerThread>();
	public static ArrayList<SubscriptionResources> subscriptionResources = new ArrayList<SubscriptionResources>();

	public static ArrayList<SubscriptionSecureServerThread> subscriptionSecureServerThread = new ArrayList<SubscriptionSecureServerThread>();
	public static ArrayList<SubscriptionResources> secureSubscriptionResources = new ArrayList<SubscriptionResources>();
}
