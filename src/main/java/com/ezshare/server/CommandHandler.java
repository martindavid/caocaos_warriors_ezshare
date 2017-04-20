package com.ezshare.server;

import com.ezshare.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;

public class CommandHandler {
	
	Message message;
	public CommandHandler(Message message) {
		this.message = message;
	}
	
	public String processMessage() throws JsonProcessingException {
		String responseMessage = "";
		switch (this.message.command.toLowerCase()) {
			case Constant.PUBLISH:
				responseMessage = new Publish(this.message.resource).processResourceMessage();
				break;
			case Constant.REMOVE:
				break;
			case Constant.SHARE:
				break;
			case Constant.FETCH:
				break;
			case Constant.EXCHANGE:
				break;
			default:
				break;
		}
		
		return responseMessage;
	}

}
