package com.ezshare;

import java.io.IOException;

import org.pmw.tinylog.Logger;

import com.ezshare.client.TCPClient;

public class Client {
	
	public static void main(String[] main) {
		Logger.info("Client is running");
		TCPClient client = new TCPClient();
		try {
			client.Execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
