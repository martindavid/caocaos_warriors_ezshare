package com.ezshare.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.ezshare.Message;

/**
 * Created by mvalentino on 20/3/17.
 * 
 * Class that handles client message construction and send it to the server
 */
public class TCPClient {

	private Message message;
	private int portNumber;
	private String hostName;
	
	public TCPClient(int portNumber, String hostName, Message message) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
	}
	
    public void Execute() throws IOException {
        try (
                Socket echoSocket = new Socket(hostName, portNumber);
        		DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());
        ) {
        	// Log it first and send to the server
        	Logger.info("My message: " + message.toJson());
            streamOut.writeUTF(message.toJson());
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

    }
}
