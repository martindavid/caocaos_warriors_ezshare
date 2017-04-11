package com.ezshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Arrays;

public class FileTransfer {
	InputStream streamin;
	OutputStream streamout;
	String filePath;
	File file;
	Socket socket;
	
	long size = 0;
	public FileTransfer(Socket s, String FilePath) throws IOException{
		streamin = s.getInputStream();
		streamout = s.getOutputStream();
		filePath = FilePath;
		file = new File(filePath);
		this.socket = s;
	}
	public void close() throws IOException{
		streamin.close();
		streamout.close();
		
	}
	public void send() throws IOException{
		try{
			RandomAccessFile byteFile = new RandomAccessFile(file,"r");
			byte[] buffer = new byte[1024*1024];
			int subsize;
			while((subsize = byteFile.read(buffer)) >0){
				streamout.write(Arrays.copyOf(buffer, subsize));
				streamout.flush();
				size += subsize;
			}
		}catch(IOException e) {
			streamout.close();
			
			
		}
	}
	public void receive() throws IOException{
		try{
			String fileLocation = "client_file";
			RandomAccessFile downloadingFile = new RandomAccessFile(fileLocation, "rw");
			long fileSizeRemaining = file.length();
			int chunkSize = 1024*1024;
			if(fileSizeRemaining < chunkSize) chunkSize = (int) fileSizeRemaining;
			byte[] buffer = new byte[chunkSize];
			int num = -1;
			while((num = streamin.read(buffer)) >0){
				downloadingFile.write(Arrays.copyOf(buffer, num));
				fileSizeRemaining -=num;
				chunkSize = 1024*1024;
				if(fileSizeRemaining < chunkSize) chunkSize = (int) fileSizeRemaining;
				buffer = new byte[chunkSize];
				if(fileSizeRemaining == 0){
					break;
				}
			}
			streamin.close();
			downloadingFile.close();
			socket.close();
		}catch (IOException e){
			streamin.close();
			socket.close();
		}
	}
	public boolean isClosed(){
		if (socket.isClosed() && socket.isConnected()){
			return true;
		}else{
			return false;
		}
	}
	public long getFileSize(){
		return file.length();
	}
	public long getDealSize(){
		return size;
	}

}