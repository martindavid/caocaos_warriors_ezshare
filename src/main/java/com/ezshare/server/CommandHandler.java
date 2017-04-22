package com.ezshare.server;

import java.util.Arrays;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;

public class CommandHandler {

	Message message;
	String serverSecret;

	public CommandHandler(Message message, String serverSecret) {
		this.message = message;
		this.serverSecret = serverSecret;
	}

	public String processMessage() throws JsonProcessingException {
		String responseMessage = "";
		switch (this.message.command.toLowerCase()) {
		case Constant.PUBLISH:
			responseMessage = new Publish(this.message.resource).processResourceMessage();
			Logger.debug("PUBLISH: response message: " + responseMessage);
			Logger.debug("PUBLISH: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.REMOVE:
			responseMessage = new RemoveCommand(this.message.resource).processResource();
			Logger.debug("REMOVE: response message: " + responseMessage);
			Logger.debug("REMOVE: Resource Size: " + Storage.resourceList.size());
			break;
		case Constant.SHARE:
			responseMessage = new ShareCommand(this.message.resource, this.message.secret, this.serverSecret)
					.processResourceMessage();
			break;
		case Constant.QUERY: // TODO: refactor this
			break;
		case Constant.FETCH:
			// TODO:Refactor FETCH method from ServerThread
			break;
		case Constant.EXCHANGE:
			responseMessage = new ExchangeCommand(this.message.serverList).processCommand();
			Logger.debug("EXCHANGE: response message: " + responseMessage);
			Logger.debug("EXCHANGE: Server List: " + Arrays.toString(Storage.serverList.toArray()));
			break;
		default:
			responseMessage = Utilities.getReturnMessage(6);
			break;
		}

		return responseMessage;
	}
}
