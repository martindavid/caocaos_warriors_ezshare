package com.ezshare;
import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resource {
	public String name;
	public String description;
	public String[] tags = new String[0];
	public String uri;
	public String channel;
	public String owner;
	public String ezserver;
	public static ArrayList<Resource> resourceList=new ArrayList<Resource>(); 
	
	@JsonView(Views.FileReceive.class)
	public long resourceSize;
	
	public static void addResource(Resource res){
		resourceList.add(res);
	}
	public static void deleteResource(Resource res){
		resourceList.remove(res);
	}
	public Resource() {
	}
	/***
	 * Copy Constructor to deep copy it
	 * @param toCopy
	 */
	public Resource(Resource toCopy){
		toCopy.name=this.name;
		toCopy.description=this.description;
		toCopy.uri=this.uri;
		toCopy.channel=this.channel;
		toCopy.owner=this.owner;
		toCopy.ezserver=this.ezserver;
		toCopy.tags=this.tags;
		toCopy.resourceSize=this.resourceSize;
		
		
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
