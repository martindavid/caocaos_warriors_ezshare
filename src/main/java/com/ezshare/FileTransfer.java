package com.ezshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileTransfer {
	FileInputStream filein;
	FileOutputStream fileout;
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
			filein = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int subsize;
			while((subsize = filein.read(buffer)) != -1){
				streamout.write(buffer);
				streamout.flush();
				size += subsize;
			}
		}catch(IOException e) {
			streamout.close();
			
			
		}
	}
	public void receive() throws IOException{
		try{
			fileout = new FileOutputStream(file);
			int subsize;
			byte[] buffer = new byte[1024];
			while((subsize = streamin.read(buffer)) != -1){
				fileout.write(buffer);
				fileout.flush();
				size += subsize;
			}
			streamin.close();
			fileout.close();
			socket.close();
		}catch (IOException e){
			streamin.close();
			fileout.close();
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
