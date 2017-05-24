package com.ezshare.server.model;

import java.util.ArrayList;

import EZShare.Resource;

public class SubscriptionResources {
	public Resource resourceTemplate;
	public ArrayList<Resource> resources;
	
	public SubscriptionResources(Resource resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
		this.resources = new ArrayList<Resource>();
	}
}
