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
	Boolean isSecure;

	public CommandHandler(Message message, DataOutputStream streamOut, String serverSecret, Boolean isSecure) {
		this.message = message;
		this.streamOut = streamOut;
		this.serverSecret = serverSecret;
		this.isSecure = isSecure;
	}

	public void processMessage() throws IOException {
		String responseMessage = "";
		switch (this.message.command.toLowerCase()) {
		case Constant.PUBLISH:
			responseMessage = new Publish(this.message.resource, this.isSecure).processResourceMessage();
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
			responseMessage = new Share(this.message.resource, this.message.secret, this.serverSecret, this.isSecure)
					.processResourceMessage();
			streamOut.writeUTF(responseMessage);
			Logger.debug("SHARE: response message: " + responseMessage);
			Logger.debug("SHARE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.QUERY:
			ArrayList<Resource> resourceList = new Query(message.resourceTemplate, message.relay,
					isSecure ? Storage.secureServerList : Storage.serverList).getResourceList();
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
			new Subscription(streamOut, message, isSecure).unsubscribe();
			break;
		case Constant.FETCH:
			Fetch fetch = new Fetch(this.message, streamOut);
			fetch.processFetch();
			break;
		case Constant.EXCHANGE:
			responseMessage = new Exchange(this.message.serverList).processCommand(isSecure);
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
