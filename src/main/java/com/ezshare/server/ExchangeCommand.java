package com.ezshare.server;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ExchangeCommand {
	private ServerList[] listServer;

	public ExchangeCommand(ServerList[] listServer) {
		this.listServer = listServer;
	}

	public String processCommand() throws JsonProcessingException {
		boolean found = false;
		boolean added = false;
		if (this.listServer.length <= 0) {
			return Utilities.getReturnMessage(9);
		}
		for (ServerList objectList : this.listServer) {
			for (ServerList systemList : Storage.serverList) {
				if (objectList.port == systemList.port && objectList.hostname.equals(systemList.hostname)) {
					found = true;
					continue;
				}
			}
			if (found == true) {
				found = false;
				continue;
			} else {
				Storage.serverList.add(objectList);
				added = true;
			}
		}
		if (added == false) {
			return Utilities.getReturnMessage(9);
		} else {
			return Utilities.getReturnMessage(1);
		}
	}

}
