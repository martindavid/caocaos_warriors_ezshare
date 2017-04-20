package com.ezshare.server;

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
			break;
		case Constant.REMOVE:
			responseMessage = new RemoveCommand(this.message.resource).processResource();
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
			break;
		default:
			responseMessage = Utilities.messageReturn(6);
			break;
		}

		return responseMessage;
	}
}
