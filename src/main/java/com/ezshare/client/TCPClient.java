package com.ezshare.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
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
	private boolean isDebug;
	private FileTransfer fileTransfer;
	private DataInputStream streamIn = null;
	
	public TCPClient(int portNumber, String hostName, Message message, boolean isDebug) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.message = message;
		this.isDebug = isDebug;
	}
	
    public void Execute() throws IOException {
        try (
                Socket echoSocket = new Socket(hostName, portNumber);
        		DataOutputStream streamOut = new DataOutputStream(echoSocket.getOutputStream());
        ) {
        	if (isDebug) {
        		// Print all of the information
        		Logger.info("Connect on port: " + portNumber);
        		Logger.info("Connect to hostname: " + hostName);
        	}
        	// Log it first and send to the server
        	
        	
        	
        	
        	Logger.info("Message :" + message.toJson());
            streamOut.writeUTF(message.toJson());
            if (message.resource.uri.length()>0)
            {
            	Socket fileSocket = new Socket(hostName, portNumber);
        		DataOutputStream filestreamOut = new DataOutputStream(fileSocket.getOutputStream());
        		if (message.command == Constant.PUBLISH || message.command == Constant.SHARE){
        	          fileTransfer = new FileTransfer(fileSocket, message.resource.uri);
        	          fileTransfer.send();
        	          fileTransfer.close();
        	         }else if(message.command == Constant.FETCH){
        	          fileTransfer = new FileTransfer(fileSocket, message.resource.uri);
//        	          message.resource.resourceSize = fileTransfer.getFileSize();
        	          fileTransfer.receive();
        	          fileTransfer.close();}
            }
            
            
            
    		String message = "";
    		try{
    			streamIn = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()));
    			while(true) {
    				if (streamIn.available() > 0) {
    					message = streamIn.readUTF();
    					System.out.println(message);}}
    			
    			
    			
//    			streamOut = new DataOutputStream(socket.getOutputStream());
//    			streamOut.writeUTF(message);
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
