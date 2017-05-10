package com.ezshare.server;

import java.io.BufferedWriter;
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
public class SecureCommandHandler {

	Message message;
	String serverSecret;
	DataOutputStream streamOut;
	BufferedWriter bufferedwriter;
	

	public SecureCommandHandler(Message message, DataOutputStream streamOut, String serverSecret, BufferedWriter bufferedwriter) {
		this.message = message;
		this.streamOut = streamOut;
		this.serverSecret = serverSecret;
		this.bufferedwriter = bufferedwriter;
	}

	public void processMessage() throws IOException {
		String responseMessage = "";
		switch (this.message.command.toLowerCase()) {
		case Constant.PUBLISH:
			responseMessage = new Publish(this.message.resource).processResourceMessage();
			Logger.debug("PUBLISH: response message: " + responseMessage);
			Logger.debug("PUBLISH: Resource Size: " + Storage.resourceList.size());
			bufferedwriter.write(responseMessage);
			bufferedwriter.flush();
			break;
		case Constant.REMOVE:
			responseMessage = new Remove(this.message.resource).processResource();
			Logger.debug("REMOVE: response message: " + responseMessage);
			Logger.debug("REMOVE: Resource Size: " + Storage.resourceList.size());
			bufferedwriter.write(responseMessage);
			bufferedwriter.flush();
			break;
		case Constant.SHARE:
			responseMessage = new Share(this.message.resource, this.message.secret, this.serverSecret)
					.processResourceMessage();
			Logger.debug("SHARE: response message: " + responseMessage);
			Logger.debug("SHARE: Resource Size: " + Storage.resourceList.size());
			bufferedwriter.write(responseMessage);
			bufferedwriter.flush();
			break;
		case Constant.QUERY:
			Query query = new Query(message.resourceTemplate, message.relay);
			ArrayList<Resource> resourceList = query.getResourceList();
			String successResponse = new Responses().toJson();
			bufferedwriter.write(successResponse);
			bufferedwriter.flush();
			if (resourceList.size() > 0) {
				for (Resource res : resourceList) {
					bufferedwriter.write(res.toJson());
					bufferedwriter.flush();
				}
			}
			bufferedwriter.write("{\"resultSize\":" + resourceList.size() + "}");
			bufferedwriter.flush();
			break;
		case Constant.FETCH:
			SecureFetch fetch = new SecureFetch(this.message, streamOut, bufferedwriter);
			fetch.processFetch();
			break;
		case Constant.EXCHANGE:
			responseMessage = new Exchange(this.message.serverList).processCommand();
			Logger.debug("EXCHANGE: response message: " + responseMessage);
			Logger.debug("EXCHANGE: Server List Size: " + Storage.serverList.size());
			bufferedwriter.write(responseMessage);
			bufferedwriter.flush();
			break;
		default:
			responseMessage = Utilities.getReturnMessage(Constant.INVALID_COMMAND);
			bufferedwriter.write(responseMessage);
			bufferedwriter.flush();
			break;
		}
	}
}

