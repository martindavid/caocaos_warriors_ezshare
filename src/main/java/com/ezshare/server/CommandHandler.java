package com.ezshare.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.Resource;

public class CommandHandler {

	Message message;
	String serverSecret;
	DataOutputStream streamOut;

	public CommandHandler(Message message, DataOutputStream streamOut, String serverSecret) {
		this.message = message;
		this.streamOut = streamOut;
		this.serverSecret = serverSecret;
	}

	public void processMessage() throws IOException {
		String responseMessage = "";
		switch (this.message.command.toLowerCase()) {
		case Constant.PUBLISH:
			responseMessage = new Publish(this.message.resource).processResourceMessage();
			streamOut.writeUTF(responseMessage);
			Logger.debug("PUBLISH: response message: " + responseMessage);
			Logger.debug("PUBLISH: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.REMOVE:
			responseMessage = new RemoveCommand(this.message.resource).processResource();
			streamOut.writeUTF(responseMessage);
			Logger.debug("REMOVE: response message: " + responseMessage);
			Logger.debug("REMOVE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.SHARE:
			responseMessage = new ShareCommand(this.message.resource, this.message.secret, this.serverSecret)
					.processResourceMessage();
			streamOut.writeUTF(responseMessage);
			Logger.debug("SHARE: response message: " + responseMessage);
			Logger.debug("SHARE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.QUERY:
			Query query = new Query(message.resourceTemplate, message.relay);
			ArrayList<Resource> resourceList = query.getResourceList();
			String successResponse = new Responses().toJson();
			streamOut.writeUTF(successResponse);
			if (resourceList.size() > 0) {
				for (Resource res : resourceList) {
					streamOut.writeUTF(res.toJson());
				}
			}
			streamOut.writeUTF("{\"resultSize\":" + resourceList.size() + "}");
			break;
		case Constant.FETCH:
			Fetch fetch = new Fetch(this.message, streamOut);
			fetch.processFetch();
			break;
		case Constant.EXCHANGE:
			responseMessage = new ExchangeCommand(this.message.serverList).processCommand();
			streamOut.writeUTF(responseMessage);
			Logger.debug("EXCHANGE: response message: " + responseMessage);
			Logger.debug("EXCHANGE: Server List Size: " + Storage.serverList.size());
			break;
		default:
			responseMessage = Utilities.getReturnMessage(6);
			streamOut.writeUTF(responseMessage);
			break;
		}
	}
}
