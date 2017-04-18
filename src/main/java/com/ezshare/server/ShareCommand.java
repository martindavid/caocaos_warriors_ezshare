package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.Object;
import org.apache.commons.validator.routines.UrlValidator;

public class ShareCommand {
	private Resource resource;
	private String secret;

	public ShareCommand(Resource resource,String secret) {
		this.secret=secret;
		this.resource = resource;
	}
	public boolean uriValidator(String uri)
	{
		String[] schemes = {"http","https"};
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (urlValidator.isValid(uri)) 
		{
			//Means this is an URL so not working for URI
			return true;
		} else 
		{
		    return false;
		}
	}
	public String processResourceMessage() throws JsonProcessingException
	{
		Resource res = this.resource;
		if(res==null)
		{
			return Utilities.messageReturn(4);
		}
		String currentSecret="";
		
		//TODO get secret
		//Validate Secret
		if(this.secret.isEmpty())
		{
			return Utilities.messageReturn(11);
		}
		if(!this.secret.equals(currentSecret))
		{
			return Utilities.messageReturn(10);
		}
		//Validate URI
		if(uriValidator(res.uri))
		{
			return Utilities.messageReturn(12);
		}
		else
		{
			Publish publish=new Publish(this.resource);
			return publish.processResourceMessage();
		}
		
	}

}
