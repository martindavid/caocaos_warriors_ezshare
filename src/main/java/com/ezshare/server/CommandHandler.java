package com.ezshare.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import EZShare.Constant;
import EZShare.Resource;

/**
 * Class to distribute command processing task
 *
 */
public class CommandHandler {

	Message message;
	String serverSecret;
	DataOutputStream streamOut;
	Boolean secure;

	public CommandHandler(Message message, DataOutputStream streamOut, String serverSecret, Boolean secure) {
		this.message = message;
		this.streamOut = streamOut;
		this.serverSecret = serverSecret;
		this.secure = secure;
	}

	public void processMessage() throws IOException {
		String responseMessage = "";
		if(this.message.command.toLowerCase().equals(Constant.SUBSCRIBE))
		{
			Subscribe subscribe = new Subscribe(message.resourceTemplate, message.relay,message.id);
			ArrayList<Resource> resourceList = subscribe.getResourceList();
			Storage.id=message.id;
			String successResponse = new Response().toJson();
			streamOut.writeUTF(successResponse);
			
			if (resourceList.size() > 0) {
				for (Resource res : resourceList) {
					streamOut.writeUTF(res.toJson());
					Storage.subscribeResultList.add(res);
				}
			}
			
		}
		
		
		else{
		switch (this.message.command.toLowerCase()) {
		case Constant.PUBLISH:
			responseMessage = new Publish(this.message.resource).processResourceMessage();
			streamOut.writeUTF(responseMessage);
			Logger.debug("PUBLISH: response message: " + responseMessage);
			Logger.debug("PUBLISH: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.REMOVE:
			responseMessage = new Remove(this.message.resource).processResource();
			streamOut.writeUTF(responseMessage);
			Logger.debug("REMOVE: response message: " + responseMessage);
			Logger.debug("REMOVE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.SHARE:
			responseMessage = new Share(this.message.resource, this.message.secret, this.serverSecret)
					.processResourceMessage();
			streamOut.writeUTF(responseMessage);
			Logger.debug("SHARE: response message: " + responseMessage);
			Logger.debug("SHARE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.QUERY:
			ArrayList<Resource> resourceList;
			if (secure = true) {
				Query query = new Query(message.resourceTemplate, message.relay);
				resourceList = query.getResourceList();
			} else {
				SecureQuery query = new SecureQuery(message.resourceTemplate, message.relay);
				resourceList = query.getResourceList();
			}
			String successResponse = new Responses().toJson();
			streamOut.writeUTF(successResponse);
			if (resourceList.size() > 0) {
				for (Resource res : resourceList) {
					streamOut.writeUTF(res.toJson());
				}
			}
			streamOut.writeUTF("{\"resultSize\":" + resourceList.size() + "}");
			break;
			
			
		case Constant.UNSUBSCRIBE:
			streamOut.writeUTF("{\"resultSize\":" + Storage.subscribeResultList.size() + "}");
			break;
		
		case Constant.FETCH:
			Fetch fetch = new Fetch(this.message, streamOut);
			fetch.processFetch();
			break;
		case Constant.EXCHANGE:
			if (secure = true) {
				responseMessage = new Exchange(this.message.serverList).processCommand();
			} else {
				responseMessage = new SecureExchange(this.message.serverList).processCommand();
			}
			streamOut.writeUTF(responseMessage);
			Logger.debug("EXCHANGE: response message: " + responseMessage);
			Logger.debug("EXCHANGE: Server List Size: " + Storage.serverList.size());
			break;
		default:
			responseMessage = Utilities.getReturnMessage(Constant.INVALID_COMMAND);
			streamOut.writeUTF(responseMessage);
			break;
		}
	}
		
	}
}
