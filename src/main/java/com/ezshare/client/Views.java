package com.ezshare.client;

/**
 * Decorator class used by JSON parser to dynamically convert object to json string
 *
 */
public class Views {
	public static class Command {
	};

	public static class Common extends Command {
	};

	public static class Fetch extends Command {
	};

	public static class Query extends Fetch {
	};

	public static class Subscribe extends Query {
	};
	
	public static class Share extends Common {
	};

	public static class Exchange extends Command {
	};

	public static class FileReceive extends Common {
	}

	public static class Response {
	};

	
	public static class ErrorMessage extends Response {
	};

}