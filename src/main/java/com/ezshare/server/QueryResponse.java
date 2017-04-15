package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Resource;

public class QueryResponse {
	public String responseMessage;
	public ArrayList<Resource> resultList=new ArrayList<Resource>();
	
	public QueryResponse(String message,ArrayList<Resource> list){
		this.responseMessage=message;
		this.resultList=list;
	}
	public QueryResponse(String message){
		this.responseMessage=message;
		this.resultList=new ArrayList<Resource>(0);;
	}
	
	
	public String getResponseMessage() {
		return responseMessage;
	}

	public ArrayList<Resource> getResultList() {
		return resultList;
	}

	
}
