package com.ezshare.server;

import java.io.IOException;

import com.ezshare.PrivateKey;
import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pmw.tinylog.Logger;

public class Utilities {

	public static void main(String[] args) 
	{
		
	}
	
	/*** Returns an object of the Class Message ***/
	public Message toMessageObject (String messageJson)
	{
    	ObjectMapper mapper = new ObjectMapper();
    	Message obj=null;
		try 
		{
			obj = mapper.readValue(messageJson, Message.class);
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
    	return obj;	
	}
	
	/*** Returns an Object of the class Resources***/
	public Resource toResourceObject (String resourceJson){
    	ObjectMapper mapper = new ObjectMapper();
    	Resource obj=null;
		try 
		{
			obj = mapper.readValue(resourceJson, Resource.class);
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
    	return obj;	
		
	}
	
	/*** Returns an Object of the class Private Key***/
	public PrivateKey toPrivateKeyObject (String keyJson){
    	ObjectMapper mapper = new ObjectMapper();
    	PrivateKey obj=null;
		try 
		{
			obj = mapper.readValue(keyJson, PrivateKey.class);
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
    	return obj;	
		
	}
	/***
	 * Checks if String has Single Star character
	 * @param owner
	 * @return
	 */
    public static boolean isSingleStar(String owner)
    {
    	if (owner.equals("*")==true)
    	{
    		return true;
    	}
    	return false;
    }
	/*** Print assistant ***/
	public static String messageReturn(int type) throws JsonProcessingException{
    	if (type==1)
    	{
    		Responses resp=new Responses();
    		resp.response="success";
    		return resp.toJson();
    	}
    	else if(type==2)
    	{
    		Responses resp=new Responses();
    		resp.response="error";
    		resp.errorMessage="cannot publish resource";
    		return resp.toJson();
    	}
    	else if (type==3)
    	{
    		Responses resp=new Responses();
    		resp.response="error";
    		resp.errorMessage="invalid resource";
    		return resp.toJson();
    	}
    	else if (type==4)
    	{
    		Responses resp=new Responses();
    		resp.response="error";
    		resp.errorMessage="missing resource";
    		return resp.toJson();
    	}
    	else //if (type ==5)
    	{
    		Responses resp=new Responses();
    		resp.response="error";
    		resp.errorMessage="cannot remove resource";
    		return resp.toJson();
    	}
    	else 
    	{
    		return "";
    	}
    }
	
	/*** Code to check if it starts or ends with Whitespace
	 * Reference:http://stackoverflow.com/questions/4067809/how-can-i-find-whitespace-space-in-a-string
	***/
    public static boolean containsWhiteSpace(final String testCode){
        if (Character.isWhitespace(testCode.charAt(0)) || Character.isWhitespace(testCode.charAt(testCode.length()-1)))
                {
                    return true;
                }
        else if (testCode.contains("\0"))
        {
        	return true;
        }
        return false;
    }
    
    public static boolean isEmpty(final String testCode){
    	if(testCode == null || testCode.isEmpty()){
    		return true;
    	}
    	else
    		return false;
    }
    /***
     * Validates if String has *
     * @param testString
     * @return
     */
    public static boolean ownerValidation(String testString){
    	if(testString.equals("*")){return true;}
    	else return false;
    }
    /***
     * Creates a new resource when the conditions are correct
     * @param resJson
     * @return
     * @throws JsonProcessingException
     */
    public String publishCommand(String resJson) throws JsonProcessingException{

    	//How do I create a new resource to append it
    	Resource res=toResourceObject(resJson);
    	//Check String values
    	if (containsWhiteSpace(res.description)||containsWhiteSpace(res.name)||containsWhiteSpace(res.channel)
    			|| containsWhiteSpace(res.owner))
	    	{
	    		return messageReturn(2);
	    	}
    	//Check for present URI
    	if(isEmpty(res.uri)) { return messageReturn(2); }
    	//Check for Owner
    	if(res.owner.equals("*")){return messageReturn(2);}
    	
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
            		return messageReturn(1);
            		
                }
            //Check for primary key differences
            else if (resourceIterator.channel.equals(res.channel) && resourceIterator.uri.equals(res.uri)
            		&& resourceIterator.owner.equals(res.owner)==false)
	            {
            		return messageReturn(2);
            		
	            }
            }
    	return messageReturn(3);
    }
    /***
     * Deletes a Resource Object when the conditions meet
     * @param resJson
     * @return
     * @throws JsonProcessingException
     */
    public String removeCommand(String resJson) throws JsonProcessingException{
    	Resource res=toResourceObject(resJson);
    	
    	for(Resource resourceIterator:Resource.resourceList)
    	{
    		if(resourceIterator.owner.equals(res.owner) && resourceIterator.channel.equals(res.channel) 
            		&& resourceIterator.uri.equals(res.uri))
    		{
    			Resource.deleteResource(res);
    			return messageReturn(1);
    		}
    		if(ownerValidation(res.owner))
    		{
    			return messageReturn(3);
    		}
    	}
    	return messageReturn(6);
    	
    }
    
    /***
     * Query for a resource
     * @param resJson
     * @return
     * @throws JsonProcessingException
     */
    public String queryCommand(String resJson) throws JsonProcessingException{
    	Resource res=toResourceObject(resJson);
    	for(Resource resourceIterator:Resource.resourceList)
    	{
    		//Template Channel equals Resource Channel
    		if(resourceIterator.channel.equals(res.channel))
    		{
    			//Template contains owner and matches
    			if (resourceIterator.owner.equals(res.owner))
    			{
    				
    			}
    			//Template does not contain Owner

    		}
    	}
    	return "Not Found";
    	
    	
    }
    

}
