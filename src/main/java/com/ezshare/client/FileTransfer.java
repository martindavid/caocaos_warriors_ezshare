package com.ezshare.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.Resource;
import com.ezshare.server.Utilities;

/**
 * A class that handle transferring files
 *
 */
public class FileTransfer {
	File file;
	DataInputStream streamIn;
	DataOutputStream streamOut;

	long size = 0;
	
	public FileTransfer(DataInputStream streamIn, DataOutputStream streamOut, String filePath) {
		this.streamIn = streamIn;
		this.streamOut = streamOut;
		this.file = new File(filePath);
	}
	
	public long getFileSize() {
		return file.length();
	}
	
	public void send() throws IOException {
		try (RandomAccessFile byteFile = new RandomAccessFile(file, "r"))	
		{
			byte[] buffer = new byte[1024 * 1024];
			int subsize;
			while ((subsize = byteFile.read(buffer)) > 0) {
				streamOut.write(Arrays.copyOf(buffer, subsize));
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	public void receiveFile() throws IOException {
		String message = "";
		try
		{
			// Read resource or result size response
			message = streamIn.readUTF();
			Logger.info(message);
				
			//file receive
			if(!message.contains(Constant.RESULT_SIZE)){
				Resource resource = Utilities.convertJsonToObject(message, Resource.class);
				String fileName = resource.uri.substring(resource.uri.lastIndexOf("/") + 1);
					
				long fileSizeRemaining = resource.resourceSize;
				int chunkSize = setChunkSize(fileSizeRemaining);
				byte[] buffer = new byte[chunkSize];
				int num;
					
				try (RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw")) {
					Logger.info("Downloading file");
					while ((num = streamIn.read(buffer)) > 0) {
						downloadingFile.write(Arrays.copyOf(buffer, num));
						fileSizeRemaining -= num;
						chunkSize = setChunkSize(fileSizeRemaining);
						buffer = new byte[chunkSize];
						if (fileSizeRemaining == 0) {
							break;
						}
					}
					Logger.info("File received!");
				}
				catch(Exception e) {
					Logger.error(e);
				}
					
				// Read result size response
				message = streamIn.readUTF();
				Logger.info(message);
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
}