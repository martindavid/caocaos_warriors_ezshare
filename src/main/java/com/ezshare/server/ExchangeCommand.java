package com.ezshare.server;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ExchangeCommand {
	private ServerList[] listServer;

	public ExchangeCommand(ServerList[] listServer) {
		this.listServer=listServer;
	}
	
	public String processCommand() throws JsonProcessingException
	{
		boolean found=false;
		boolean added=false;
		if (this.listServer.length<=0)
		{
			return Utilities.messageReturn(9);
		}
		for (ServerList objectList : this.listServer)
		{
			for (ServerList systemList : TCPServer.serverList)
			{
				if(objectList.portNumber==systemList.portNumber && objectList.hostname.equals(systemList.hostname))
				{
					found=true;
					continue;
				}
			}
			if (found==true)
			{
				found=false;
				continue;
			}
			else
			{
				TCPServer.serverList.add(objectList);
				added=true;
			}
		}
		if (added==false)
		{
			return Utilities.messageReturn(9);
		}
		else
		{
			return Utilities.messageReturn(1);
		}
	}

}
