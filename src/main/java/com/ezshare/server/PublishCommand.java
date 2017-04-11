package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PublishCommand {
	private Resource resource;
	public PublishCommand(Resource resource)
	{
		this.resource=resource;
	}

	/***
     * Creates a new resource when the conditions are correct
     * @param resJson
     * @return
     * @throws JsonProcessingException
     */
    public String processResourceMessage() throws JsonProcessingException{
    	//String resJson=this.resource;
    	//How do I create a new resource to append it
    	//Resource res=Utilities.toResourceObject(resJson);
    	Resource res=this.resource;
    	
    	//check for empty String
    	//Check String values
    	if (Utilities.containsWhiteSpace(res.description)||Utilities.containsWhiteSpace(res.name)||Utilities.containsWhiteSpace(res.channel)
    			|| Utilities.containsWhiteSpace(res.owner))
	    	{
	    		return Utilities.messageReturn(2);
	    	}
    	//Check for present URI
    	if(Utilities.isEmpty(res.uri)) { return Utilities.messageReturn(2); }
    	//Check for Owner
    	if(res.owner.equals("*")){return Utilities.messageReturn(2);}
    	
    	for(Resource resourceIterator:Resource.resourceList){
    		//Check for same primary key and overwrite
            if(resourceIterator.owner.equals(res.owner) && resourceIterator.channel.equals(res.channel) 
            		&& resourceIterator.uri.equals(res.uri))
            	{
            		resourceIterator.ezserver=res.ezserver;
            		resourceIterator.tags=res.tags;
            		resourceIterator.description=res.description;
            		resourceIterator.name=res.name;
            		Resource.addResource(res);
            		return Utilities.messageReturn(1);
            		
                }
            //Check for primary key differences
            else if (resourceIterator.channel.equals(res.channel) && resourceIterator.uri.equals(res.uri)
            		&& resourceIterator.owner.equals(res.owner)==false)
	            {
            		return Utilities.messageReturn(2);
            		
	            }
            }
    	Resource.addResource(res);
		return Utilities.messageReturn(1);
    }
}
