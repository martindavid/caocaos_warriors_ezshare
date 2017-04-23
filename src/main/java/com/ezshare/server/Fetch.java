package com.ezshare.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Arrays;

import org.pmw.tinylog.Logger;

import com.ezshare.Constant;
import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Fetch {
	private Message message;
	private DataOutputStream streamOut;

	public Fetch(Message message, DataOutputStream streamOut) {
		this.message = message;
		this.streamOut = streamOut;
	}

	public void processFetch() throws JsonProcessingException, IOException {
		Resource resTemplate = this.message.resourceTemplate;
		Resource res = (Resource) Storage.resourceList.stream()
				.filter(x -> x.uri.equals(resTemplate.uri) && x.channel.equals(resTemplate.channel)).findAny()
				.orElse(null);

		if (res != null) {
			try {
				// Send success message
				Logger.debug("FETCH: send success message");
				streamOut.writeUTF(Utilities.getReturnMessage(Constant.SUCCESS));
				// Send file use FileTransfer class
				URI uri = new URI(res.uri);
				File file = new File(uri.getPath());
				res.resourceSize = file.length();
				// Write resource response
				Logger.debug("FETCH: send resource message");
				streamOut.writeUTF(res.toFetchResultJson());
				try (RandomAccessFile byteFile = new RandomAccessFile(file, "r")) {
					byte[] buffer = new byte[1024 * 1024];
					int subsize;
					Logger.debug("FETCH: send file");
					while ((subsize = byteFile.read(buffer)) > 0) {
						streamOut.write(Arrays.copyOf(buffer, subsize));
					}
				}
				// It's ok to hardcode this message, because we have 
				// make sure that we can only have 1 resource fetch at a time
				Logger.debug("FETCH: send resultSize message");
				streamOut.writeUTF("{\"resultSize\":1}"); 
			} catch (Exception e) {
				Logger.error(e);
				streamOut.writeUTF(Utilities.getReturnMessage(Constant.INVALID_RESOURCE_TEMPLATE));
			}
		} else {
			streamOut.writeUTF(Utilities.getReturnMessage(Constant.INVALID_RESOURCE_TEMPLATE));
		}
	}
}
