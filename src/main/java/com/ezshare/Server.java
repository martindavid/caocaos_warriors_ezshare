package com.ezshare;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;

import com.ezshare.server.TCPServer;
import com.ezshare.server.Utilities;

public class Server {

	public static void main(String[] args) {
		CommandLine cmd = new Cli(args).parseServer();

		int port = 3030;
		String hostName;
		String secret = Utilities.generateRandomString(40);
		int exchangeInterval = 600;
		int connectionIntervalLimit = 1;

		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostName = "localhost";
		}

		if (cmd.hasOption(Constant.ADVERTISED_HOSTNAME)) {
			hostName = cmd.getOptionValue(Constant.ADVERTISED_HOSTNAME);
		}

		if (cmd.hasOption(Constant.PORT)) {
			port = Integer.parseInt(cmd.getOptionValue(Constant.PORT));
		}

		if (cmd.hasOption(Constant.SECRET)) {
			secret = cmd.getOptionValue(Constant.SECRET);
		}

		if (cmd.hasOption(Constant.EXCHANGE_INTERVAL)) {
			exchangeInterval = Integer.parseInt(cmd.getOptionValue(Constant.EXCHANGE_INTERVAL));
		}
		
		if (cmd.hasOption(Constant.CONNECTION_INTERVAL_LIMIT)) {
			connectionIntervalLimit = Integer.parseInt(cmd.getOptionValue(Constant.CONNECTION_INTERVAL_LIMIT));
		}

		if (cmd.hasOption(Constant.DEBUG)) {
			Configurator.currentConfig().level(Level.DEBUG).activate();
		}

		TCPServer server = new TCPServer(hostName, port, secret, exchangeInterval, connectionIntervalLimit);
		server.start();
	}
}
