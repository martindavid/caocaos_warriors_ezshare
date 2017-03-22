package com.ezshare;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Logger;

import com.ezshare.server.TCPServer;

public class Server {
	
	public static void main(String[] args) {
		Logger.info("Server is running");
		CommandLine cmd = new Cli(args).parseServer();
		
		// TODO: put a logic to parse the argument
		if (cmd.hasOption(Constant.ADVERTISED_HOSTNAME)) {
			Logger.info("Advertised Hostname passed with value: " + cmd.getOptionValue(Constant.ADVERTISED_HOSTNAME));
		}
		
		if (cmd.hasOption(Constant.PORT)) {
			Logger.info("Port is passed with value: " + cmd.getOptionValue(Constant.PORT));
		}
	}
}
