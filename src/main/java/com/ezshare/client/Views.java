package com.ezshare.client;

public class Views {
	public static class comm{};
	public static class norm extends comm{};
	public static class Fetch extends comm{};
	public static class Query extends Fetch{};
	public static class Share extends norm{};
	public static class Exchange extends comm{};
	public static class FileReceive{};

}