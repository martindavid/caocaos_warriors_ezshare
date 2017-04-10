package com.ezshare;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resource {
	public String name;
	public String description;
	public ArrayList<String> tags;
	public String uri;
	public String channel;
	public String owner;
	public String ezserver;
	public String secret;
	public long resourceSize;
	public static ArrayList<Resource> resourceList=new ArrayList<Resource>(); 
	
	public static void addResource(Resource res){
		resourceList.add(res);
	}
	public static void deleteResource(Resource res){
		resourceList.remove(res);
	}
	public Resource() {
		this.tags = new ArrayList<String>();
	}
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
	
		return mapper.writeValueAsString(this);
	}
	
	public static Resource fromJson(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		
		Resource parsedValue = null;
		try {
			parsedValue = mapper.readValue(jsonString, Resource.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parsedValue;
	}
	
}
