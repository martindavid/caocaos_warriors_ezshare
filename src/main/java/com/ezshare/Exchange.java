package com.ezshare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Exchange {
	public String hostname;
	public int port;
	
	public Exchange() {
		
	}
	
	public Exchange(String hostName, int port){
		this.hostname = hostName;
		this.port = port;
	}

	public String toJson(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return " ";
	}

}
