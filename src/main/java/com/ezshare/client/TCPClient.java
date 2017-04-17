package com.ezshare.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.ezshare.FileTransfer;
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
	private FileTransfer fileTransfer;
	
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
        	// Print all of the information
        	Logger.info("Starting the EZShare Server");
        	Logger.info("using secret: "); // TODO: create functionality to generate secret
    		Logger.info("using advertised hostname: " + hostName);
    		Logger.info("bound to port " + portNumber);
    		Logger.info("started");
    		
    		// Log it first and send to the server
    		Logger.debug("Setting Debug On");
        	Logger.debug("[SENT]:" + message.toJson());
            streamOut.writeUTF(message.toJson());
            
     
    		String message_echo = "";
    		try (
    				DataInputStream streamIn = 
    					new DataInputStream(new BufferedInputStream(echoSocket.getInputStream())))
    		{
    			if (message.command.equals("FETCH")){
    				while(true){
    					if(streamIn.available() > 0){
    						message_echo = streamIn.readUTF();
    						System.out.println(message_echo);
    						fileTransfer = new FileTransfer(echoSocket, message.resourceTemplate.uri);
    						fileTransfer.receive();
    						message_echo = streamIn.readUTF();
    						System.out.println(message_echo);
    				}
    				}
    			}else{
    				while(true) {
    					while (streamIn.available() > 0) {
    						message_echo = streamIn.readUTF();
    						System.out.println(message_echo);
    				}
    			}
    			}
    			}
    		
    		catch (IOException ioe) {
    			// TODO: handle exception
    			Logger.error(ioe);
    		}
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
        catch(Exception e) {
        	Logger.error(e);
        }

    }
}
