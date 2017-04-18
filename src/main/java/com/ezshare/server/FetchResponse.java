package com.ezshare.server;

import com.ezshare.Resource;
//  the response type from the sever
public class FetchResponse {
	public String responseMessage;
	public Resource res;
	public addmessage adsize = new addmessage();
	
	public FetchResponse(String message,Resource resource){
		this.responseMessage = message;
		this.res = resource;
		adsize.resultSize = 1;
	}
	public FetchResponse(String message){
		this.responseMessage = message;
		adsize.resultSize = 0;
	}
	
	
	public String getResponseMessage() {
		return responseMessage;
	}

	public Resource getResource() {
		return res;
	}
	
	

}
