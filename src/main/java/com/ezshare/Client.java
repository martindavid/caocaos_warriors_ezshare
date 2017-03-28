package com.ezshare;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Logger;

import com.ezshare.client.TCPClient;

public class Client {
	
	public static void main(String[] args) throws IOException {
		Logger.info("Client is running");
		Message message = new Message();
		int portNumber = 0;
		String hostName = "localhost";
		
		CommandLine cmd = new Cli(args).parseClient();
		
		if (cmd.hasOption(Constant.PUBLISH)) {
			message.command = Constant.PUBLISH;
		} else if (cmd.hasOption(Constant.REMOVE)) {
			message.command = Constant.REMOVE;
		} else if (cmd.hasOption(Constant.SHARE)) {
			message.command = Constant.SHARE;
		} else if (cmd.hasOption(Constant.QUERY)) {
			message.command = Constant.QUERY;
		} else if (cmd.hasOption(Constant.FETCH)) {
			message.command = Constant.FETCH;
		} else if (cmd.hasOption(Constant.EXCHANGE)) {
			message.command = Constant.EXCHANGE;
		}
		
		
		if (cmd.hasOption(Constant.NAME)) {
			message.resource.name = cmd.getOptionValue(Constant.NAME);
		}
		
		if (cmd.hasOption(Constant.DESCRIPTION)){
			message.resource.description = cmd.getOptionValue(Constant.DESCRIPTION);
		}
		
		if (cmd.hasOption(Constant.TAGS)) {
			message.resource.tags = cmd.getOptionValues(Constant.TAGS);
		}
		
		if (cmd.hasOption(Constant.URI)) {
			message.resource.uri = cmd.getOptionValue(Constant.URI);
		}
		
		if (cmd.hasOption(Constant.CHANNEL)) {
			message.resource.channel = cmd.getOptionValue(Constant.CHANNEL);
		}
		
		if (cmd.hasOption(Constant.OWNER)) {
			message.resource.owner = cmd.getOptionValue(Constant.OWNER);
		}
		
		if (cmd.hasOption(Constant.PORT)) {
			portNumber = Integer.parseInt(cmd.getOptionValue(Constant.PORT));
		}
		
		if (cmd.hasOption(Constant.HOST)) {
			hostName = cmd.getOptionValue(Constant.HOST);
		}
		if (cmd.hasOption(Constant.SECRET)) {
			message.resource.secret = cmd.getOptionValue(Constant.SECRET);
		}
		
		if (cmd.hasOption(Constant.SERVERS)) {
			message.resource.ezserver = cmd.getOptionValue(Constant.SERVERS);
		}
		
		TCPClient client = new TCPClient(portNumber, hostName, message);
		client.Execute();
	}
}
