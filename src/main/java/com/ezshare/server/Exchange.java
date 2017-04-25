package com.ezshare.server;

import java.util.ArrayList;

import com.ezshare.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Exchange {
	private ArrayList<ServerList> listServer;

	public Exchange(ArrayList<ServerList> serverList) {
		this.listServer = serverList;
	}

	public String processCommand() throws JsonProcessingException {
		if (this.listServer.size() <= 0) {
			return Utilities.getReturnMessage(Constant.MISSING_OR_INVALID_SERVER_LIST);
		}
		for (ServerList server : this.listServer) {
			ServerList existingServer = (ServerList) Storage.serverList.stream()
					.filter(x -> x.hostname.equals(server.hostname) && x.port == server.port)
					.findAny()
					.orElse(null);

			if (existingServer == null) {
				Storage.serverList.add(server);
			}
		}
		return Utilities.getReturnMessage(Constant.SUCCESS);
	}
}
