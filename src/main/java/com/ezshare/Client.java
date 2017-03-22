package com.ezshare;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Logger;

public class Client {
	
	public static void main(String[] args) {
		Logger.info("Client is running");
		CommandLine cmd = new Cli(args).parseClient();
		
		// TODO: put a logic to parse the argument
	}

}
