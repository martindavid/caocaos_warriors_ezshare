package EZShare;

import org.pmw.tinylog.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A class that handle parsing command line arguments
 * @author mvalentino
 *
 */
public class Cli {
	private String[] args = null;
	
	public Cli(String[] args) {
		this.args = args;
	}
	
	/**
	 * Construct a command line arguments for server program
	 * @return CommandLine object
	 */
	public CommandLine parseServer() {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		Options options = constructServerOptions();
		
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				help(options);
			}
			
		} catch(ParseException e) {
			help(options);
		}
		
		return cmd;
	}
	
	/**
	 * Construct a command line arguments for client program
	 * @return CommandLine object
	 */
	public CommandLine parseClient() {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		Options options = constructClientOptions();
		
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				help(options);
			}
		} catch(ParseException e) {
			Logger.error("Invalid option");
			help(options);
		}
		
		return cmd;
	}
	
	/**
	 * A command line arguments options for server program
	 * @return command line options for server
	 */
	private Options constructServerOptions() {
		Options options = new Options();
		
		options.addOption(Constant.ADVERTISED_HOSTNAME, true, "advertised hostname");
		options.addOption(Constant.CONNECTION_INTERVAL_LIMIT, true, "connection interval limit in seconds");
		options.addOption(Constant.EXCHANGE_INTERVAL, true, "exchange interval in seconds");
		options.addOption(Constant.PORT, true, "Server port, an integer");
		options.addOption(Constant.SECRET, true, "Secret");
		options.addOption(Constant.DEBUG, false, "Print debug information");
		
		return options;
	}
	
	
	/**
	 * A command line arguments options for client program
	 * @return command line options for Client
	 */
	private Options constructClientOptions() {
		Options options = new Options();
		
		options.addOption(Constant.CHANNEL, true, "channel");
		options.addOption(Constant.DEBUG, false, "print debug information");
		options.addOption(Constant.DESCRIPTION, true, "resource description");
		options.addOption(Constant.EXCHANGE, false, "exchange server list with server");
		options.addOption(Constant.FETCH, false, "fetch resources from server");
		options.addOption(Constant.HOST, true, "server host, a domain name or IP Address");
		options.addOption(Constant.NAME, true, "resource name");
		options.addOption(Constant.OWNER, true, "owner");
		options.addOption(Constant.PORT, true, "server port, an integer");
		options.addOption(Constant.PUBLISH, false, "publish resource on server");
		options.addOption(Constant.QUERY, false, "query for resources from server");
		options.addOption(Constant.REMOVE, false, "remove resource from server");
		options.addOption(Constant.SECRET, true, "secret");
		options.addOption(Constant.SERVERS, true, "server list");
		options.addOption(Constant.SHARE, false, "share resource on server");
		options.addOption(Constant.TAGS, true, "resource tag");
		options.addOption(Constant.URI, true, "resource uri");
		
		return options;
	}
	
	//TODO: what should we construct here?
	private void help(Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
		System.exit(0);
	}
}
