package com.ezshare.server;

import java.io.IOException;
import java.security.SecureRandom;

import com.ezshare.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public static String getReturnMessage(int type) throws JsonProcessingException {
		switch (type) {
		case 1:
			return new Responses().toJson();
		case 2:
			return new Responses(Constant.CANNOT_PUBLISH_RESOURCE).toJson();
		case 3:
			return new Responses(Constant.INVALID_RESOURCE).toJson();
		case 4:
			return new Responses(Constant.MISSING_RESOURCE).toJson();
		case 5:
			return new Responses(Constant.CANNOT_REMOVE_RESOURCE).toJson();
		case 6:
			return new Responses(Constant.INVALID_COMMAND).toJson();
		case 7:
			return new Responses(Constant.INVALID_RESOURCE_TEMPLATE).toJson();
		case 8:
			return new Responses(Constant.MISSING_RESOURCE_TEMPLATE).toJson();
		case 9:
			return new Responses(Constant.MISSING_OR_INVALID_SERVER_LIST).toJson();
		case 10:
			return new Responses(Constant.INCORRECT_SECRET).toJson();
		case 11:
			return new Responses(Constant.MISSING_RESOURCE_OR_SECRET).toJson();
		default:
			return new Responses(Constant.CANNOT_SHARE_RESOURCE).toJson();
		}
	}
}
