package com.ezshare.server;

import java.io.IOException;
import java.security.SecureRandom;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilities {

	/**
	 * Generate random string with specific length
	 * @param len
	 * @return random string with len length
	 */
	public static String generateRandomString(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
	
	/**
	 * Generic method to convert from jsonString to T object where T is class
	 * @param jsonString
	 * @param target
	 * @return T instance
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T convertJsonToObject(String jsonString, Class<T> target) 
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.readValue(jsonString, target);
	}

	/*** Returns an Object of the class Resources ***/
	public static Resource toResourceObject(String resourceJson) {
		ObjectMapper mapper = new ObjectMapper();
		Resource obj = null;
		try {
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

	/***
	 * Checks if String has Single Star character
	 * 
	 * @param owner
	 * @return
	 */
	public static boolean isSingleStar(String owner) {
		if (owner.equals("*") == true) {
			return true;
		}
		return false;
	}

	/*** Print assistant ***/
	public static String messageReturn(int type) throws JsonProcessingException {
		if (type == 1) {
			Responses resp = new Responses();
			resp.response = "success";
			return resp.toJson();
		} else if (type == 2) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "cannot publish resource";
			return resp.toJson();
		} else if (type == 3) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "invalid resource";
			return resp.toJson();
		} else if (type == 4) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "missing resource";
			return resp.toJson();
		} else if (type == 5) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "cannot remove resource";
			return resp.toJson();
		} else if (type == 6) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "invalid command";
			return resp.toJson();
		} else if (type == 7) {
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "invalid resourcetemplate";
			return resp.toJson();
		} else if(type ==8)
		{
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "missing resourcetemplate";
			return resp.toJson();
		}
		else if (type==9)
		{
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "missing or invalid server list";
			return resp.toJson();
		}
		else if (type==10)
		{
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "incorrect secret";
			return resp.toJson();
		}
		else if (type==11)
		{
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "missing resource and or secret";
			return resp.toJson();
		}
		else //if (type==12)
		{
			Responses resp = new Responses();
			resp.response = "error";
			resp.errorMessage = "cannot share resource";
			return resp.toJson();
		}



	}

	/***
	 * Code to check if it starts or ends with Whitespace
	 * Reference:http://stackoverflow.com/questions/4067809/how-can-i-find-whitespace-space-in-a-string
	 ***/
	public static boolean containsWhiteSpace(final String testCode) {
		if (!testCode.isEmpty() && (Character.isWhitespace(testCode.charAt(0))
				|| Character.isWhitespace(testCode.charAt(testCode.length() - 1)))) {
			return true;
		} else if (!testCode.isEmpty() && testCode.contains("\0")) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(final String testCode) {
		return (testCode == null || testCode.isEmpty());
	}

	/***
	 * Validates if String has *
	 * 
	 * @param testString
	 * @return
	 */
	public static boolean ownerValidation(String testString) {
		return (testString.equals("*"));
	}
}
