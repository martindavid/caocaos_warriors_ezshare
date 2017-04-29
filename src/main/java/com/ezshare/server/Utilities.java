package com.ezshare.server;

import java.io.IOException;
import java.security.SecureRandom;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EZShare.Constant;

public class Utilities {

	/**
	 * Generate random string with specific length
	 * 
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
	 * 
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

	/*** Print assistant ***/
	public static String getReturnMessage(String message) throws JsonProcessingException {
		return message.equals(Constant.SUCCESS) ? 
				new Responses().toJson() : new Responses(message).toJson();
	}
}
