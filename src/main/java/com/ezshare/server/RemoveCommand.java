package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RemoveCommand {
	
	private Resource resource;
	public RemoveCommand(Resource resource)
	{
		this.resource=resource;
	}
	
    /***
     * Deletes a Resource Object when the conditions meet
     * @param resJson
     * @return
     * @throws JsonProcessingException
     */
    public String processResource() throws JsonProcessingException {
    	Resource res=this.resource;
    	if(Utilities.ownerValidation(res.owner))
		{
			return Utilities.messageReturn(3);
		}
    	for(Resource resourceIterator:Resource.resourceList)
    	{
    		
    		if(resourceIterator.owner.equals(res.owner) && resourceIterator.channel.equals(res.channel) 
            		&& resourceIterator.uri.equals(res.uri))
    		{
    			Resource.deleteResource(res);
    			return Utilities.messageReturn(1);
    		}
    		
    	}
    	return Utilities.messageReturn(5);
    	
    }

}
