package com.ezshare.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.pmw.tinylog.Logger;

import com.ezshare.server.model.ConnectionTracking;
import com.ezshare.server.model.Responses;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EZShare.Constant;
import EZShare.Resource;

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

	public static Boolean isResourceMatch(Resource res, Resource template) {
		Boolean result = false;
		Logger.debug("QUERY or SUBSCRIBE: validate resource");
		Logger.debug(String.format("QUERY or SUBSCRIBE: Channel: %s, Owner: %s, Uri: %s, Name: %s, Description: %s",
				res.channel, res.owner, res.uri, res.name, res.description));

		if ((res.channel.equals(template.channel)) && (res.name.contains(template.name) || (template.name.isEmpty()))
				&& (res.description.contains(template.description) || (template.description.isEmpty()))
				&& (res.uri.contains(template.uri) || (template.uri.isEmpty()))
				&& (res.owner.contains(template.owner) || (template.owner.isEmpty()))) {
			if (template.tags.length > 0) {
				result = Arrays.asList(res.tags).containsAll(Arrays.asList(template.tags));
			} else {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Validate whether incoming IP address allowed to make a connection or not
	 * We validate against IP list array that system maintains
	 * 
	 * @param ipAddress
	 *            ip address need to validate
	 * @param connIntervalLimit
	 *            connection interval limit per ip address
	 * @return
	 */
	public static boolean isIpAllowed(String ipAddress, int connIntervalLimit) {
		ConnectionTracking tracking = (ConnectionTracking) Storage.ipList.stream()
				.filter(x -> x.ipAddress.equals(ipAddress)).findAny().orElse(null);
		if (tracking != null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date now = new Date();
			try {
				Date lastConnected = dateFormat.parse(tracking.timeStamp);
				if ((now.getTime() - lastConnected.getTime()) / 1000 % 60 <= connIntervalLimit) {
					return false;
				}
			} catch (ParseException e) {
				Logger.error(e);
			}
		}

		return true;
	}

	public static void removeIp(String ipAddress) {
		ConnectionTracking tracking = (ConnectionTracking) Storage.ipList.stream()
				.filter(x -> x.ipAddress.equals(ipAddress)).findAny().orElse(null);
		if (tracking != null) {
			Storage.ipList.remove(tracking);
		}
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
		return message.equals(Constant.SUCCESS) ? new Responses().toJson() : new Responses(message).toJson();
	}

	/**
	 * A way to set SSL certificate system wide
	 * Based on an answer here https://stackoverflow.com/a/17352927/4736604
	 * @param keyStream
	 * @param keystorePassword
	 * @param trustStream
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	public static SSLContext setSSLFactories(InputStream keyStream, String keystorePassword, InputStream trustStream)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException, KeyManagementException {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] keyPassword = keystorePassword.toCharArray();
		keyStore.load(keyStream, keyPassword);
		
		// Initialize the key manager factory with key store
		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyFactory.init(keyStore, keyPassword);
		
		KeyManager[] keyManagers = keyFactory.getKeyManagers();
		
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		
		trustStore.load(trustStream, null);
		
		// Initialize trust manager factory with trus store
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(trustStore);
		
		TrustManager[] trustManagers = trustFactory.getTrustManagers();
		
		// initialize an ssl context to use these managers and set as default
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(keyManagers, trustManagers, null);
		return sslContext;
	}
}