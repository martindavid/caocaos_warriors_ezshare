package com.ezshare.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Arrays;

import org.pmw.tinylog.Logger;

import com.ezshare.Resource;
import com.ezshare.server.Utilities;

/**
 * A class that handle transferring files
 *
 */

public class FileTransfer {
	DataInputStream streamin;
	DataOutputStream streamout;
	String filePath;
	File file;
	Socket socket;

	long size = 0;

	public FileTransfer(Socket s, String FilePath) throws IOException {
		streamin = new DataInputStream(s.getInputStream());
		streamout = new DataOutputStream(s.getOutputStream());
		filePath = FilePath;
		file = new File(filePath);
		this.socket = s;
	}

	public void close() throws IOException {
		streamin.close();
		streamout.close();
	}

	public void send() throws IOException {
		try (
			RandomAccessFile byteFile = new RandomAccessFile(file, "r")
		)	
		{
			byte[] buffer = new byte[1024 * 1024];
			int subsize;
			while ((subsize = byteFile.read(buffer)) > 0) {
				streamout.write(Arrays.copyOf(buffer, subsize));
				size += subsize;
			}
		} catch (IOException e) {
			streamout.close();
		}
	}

	public void receive() throws IOException {
		String message = "";
		try {
			if (streamin.available() > 0) {
				message = streamin.readUTF();
				Logger.info(message);
				Resource resourceObject = Utilities.toResourceObject(message);
				String fileLocation = resourceObject.name;
				RandomAccessFile downloadingFile = new RandomAccessFile(fileLocation, "rw");
				long fileSizeRemaining = resourceObject.resourceSize;
				int chunkSize = setChunkSize(fileSizeRemaining);
				byte[] buffer = new byte[chunkSize];
				int num;
				Logger.info("Downloading file");
				while ((num = streamin.read(buffer)) > 0) {
					downloadingFile.write(Arrays.copyOf(buffer, num));
					fileSizeRemaining -= num;
					chunkSize = setChunkSize(fileSizeRemaining);
					buffer = new byte[chunkSize];
					if (fileSizeRemaining == 0) {
						break;
					}
				}
				Logger.info("File received!");
				downloadingFile.close();
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private int setChunkSize(long fileSizeRemaining) {
		int chunkSize = 1024 * 1024;
		if (fileSizeRemaining < chunkSize) {
			chunkSize = (int) fileSizeRemaining;
		}
		return chunkSize;
	}

	public boolean isClosed() {
		return socket.isClosed() && socket.isConnected();
	}

	public long getFileSize() {
		return file.length();
	}

	public long getDealSize() {
		return size;
	}

}