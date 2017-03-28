package com.ezshare.server;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public Resources toResourceObject (String resourceJson){
    	ObjectMapper mapper = new ObjectMapper();
    	Resources obj=null;
		try 
		{
			obj = mapper.readValue(resourceJson, Resources.class);
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
    	else if (type ==5)
    	{
    		Responses resp=new Responses();
    		resp.response="error";
    		resp.errorMessage="cannot remove resource";
    		return resp.toJson();
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
        return false;
    }

}
