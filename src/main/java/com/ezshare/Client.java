package com.ezshare;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Logger;

import com.ezshare.client.TCPClient;

public class Client {
	
	public static void main(String[] args) throws IOException {
		Logger.info("Client is running");
		CommandLine cmd = new Cli(args).parseClient();
		
		// TODO: put a logic to parse the argument
		TCPClient client = new TCPClient();
		client.Execute();
	}
}
