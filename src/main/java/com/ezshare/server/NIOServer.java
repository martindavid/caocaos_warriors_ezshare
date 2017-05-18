package com.ezshare.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.input.BOMInputStream;
import org.pmw.tinylog.Logger;

public class NIOServer {

	private int portNumber;
	private String hostName;
	private String secret;
	private int exchangeInterval;
	private int connIntervalLimit;

	public NIOServer(String hostName, int portNumber, String secret, int exchangeInterval, int connIntervalLimit) {
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.secret = secret;
		this.exchangeInterval = exchangeInterval;
		this.connIntervalLimit = connIntervalLimit;
	}

	public void start() {
		Logger.info("Starting the EZShare Server");
		Logger.info("Using secret: " + secret);
		Logger.info("using advertised hostname: " + hostName);
		Logger.info("bound to port: " + portNumber);
		Logger.info("Waiting for a client.....");
		Logger.debug("Setting debug on");
		
		try {
			Selector selector = Selector.open();
			ServerSocketChannel socket = ServerSocketChannel.open();
			InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

			// Binds the channel's socket to a local address and configures the
			// socket to listen for connections
			socket.bind(socketAddress);
			// Adjusts this channel's blocking mode.
			socket.configureBlocking(false);

			int ops = socket.validOps();
			socket.register(selector, ops, null);
			
			// Start listening to client request
			while(true) {
				// Selects a set of keys whose corresponding channels are ready for I/O operations
				selector.select();
				// token representing the registration of a SelectableChannel with a Selector
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				
				while (iterator.hasNext()) {
					SelectionKey clientKey = iterator.next();

					// Tests whether this key's channel is ready to accept a new socket connection
					if (clientKey.isAcceptable()) {
						SocketChannel clientSocket = socket.accept();
						// Adjusts this channel's blocking mode to false
						clientSocket.configureBlocking(false);
						// Operation-set bit for read operations
						clientSocket.register(selector, SelectionKey.OP_READ);
						Logger.debug("Connection Accepted: " + clientSocket.getLocalAddress());
					}
					else if (clientKey.isReadable()) {
						SocketChannel clientSocket = (SocketChannel) clientKey.channel();
//						ByteBuffer buffer = ByteBuffer.allocate(4096);
//						clientSocket.read(buffer);
//						String message = new String(buffer.array());
//						Logger.debug(message.substring(1));
						String message = processRead(clientSocket);
						Logger.debug(message);
						clientSocket.register(selector, SelectionKey.OP_WRITE);
					}
					else if (clientKey.isWritable()) {
						SocketChannel clientSocket = (SocketChannel) clientKey.channel();
						String newData = "New String to write to file..." + System.currentTimeMillis();

						ByteBuffer buf = ByteBuffer.allocate(48);
						
						buf.clear();
						buf.put(newData.getBytes());

						buf.flip();

						while(buf.hasRemaining()) {
						    clientSocket.write(buf);
						}

						clientSocket.close();
					}
				}
				iterator.remove();
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	
	public static String processRead(SocketChannel sChannel) throws Exception {
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	    sChannel.read(buffer);
	    buffer.flip();
	    Charset charset = Charset.forName("UTF-8");
	    CharsetDecoder decoder = charset.newDecoder();
	    CharBuffer charBuffer = decoder.decode(buffer);
	    String msg = charBuffer.toString();
	    return msg;
	  }
}
