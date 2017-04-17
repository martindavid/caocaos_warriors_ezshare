package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Resource;
//  the response type from the sever
public class FetchResponse {
	private String responseMessage;
	private Resource res;
	
	public FetchResponse(String message,Resource resource){
		this.responseMessage=message;
		this.res= resource;
	}
	public FetchResponse(String message){
		this.responseMessage=message;
		this.res=new Resource();
	}
	
	
	public String getResponseMessage() {
		return this.responseMessage;
	}

	public Resource getResource() {
		return this.res;
	}
	
	

}
