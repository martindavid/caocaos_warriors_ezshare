package com.ezshare;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Logger;

import com.ezshare.server.TCPServer;
import com.ezshare.server.Utilities;

public class Server {
	
	public static void main(String[] args) {
		CommandLine cmd = new Cli(args).parseServer();
		
		int port = 3030;
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostName = "localhost";
		}
		String secret = Utilities.generateRandomString(20);
		
		// TODO: put a logic to parse the argument
		if (cmd.hasOption(Constant.ADVERTISED_HOSTNAME)) {
			hostName = cmd.getOptionValue(Constant.ADVERTISED_HOSTNAME);
		}
		
		if (cmd.hasOption(Constant.PORT)) {
			port = Integer.parseInt(cmd.getOptionValue(Constant.PORT));
		}
		
		if (cmd.hasOption(Constant.SECRET)) {
			secret = cmd.getOptionValue(Constant.SECRET);
		}
		
		TCPServer server = new TCPServer(hostName, port, secret);
		server.start();
	}
}
