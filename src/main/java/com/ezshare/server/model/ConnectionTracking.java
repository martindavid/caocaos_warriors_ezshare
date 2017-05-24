package com.ezshare.server.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectionTracking {
	public String ipAddress;
	public String timeStamp;
	
	public ConnectionTracking(String ipAddress) {
		this.ipAddress = ipAddress;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		this.timeStamp = dateFormat.format(new Date());
	}
}
