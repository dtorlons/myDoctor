package utils;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Blob;
import java.sql.SQLException;

import javax.servlet.ServletContext;

public class Archive {

	private String filename;
	private InputStream inputStream;
	
	public Archive (Blob blob, String filename) throws SQLException, IOException{		
		this.filename = filename; 
		this.inputStream = blob.getBinaryStream();
		if(inputStream == null) {
			throw new IOException();
		}
		}
	
	public int getContentLength() throws IOException {
		return inputStream.available();
	}
	
	public InputStream getInputStream() {
		return this.inputStream;
	}
	
	public String getFilename() {
		return this.filename;
	}	
	
	
	public String getHeaderKey() {
		return "Content-Disposition";
	}
	
	public String getHeaderValue() {
		return String.format("attachment; filename=\"%s\"", filename);
	}
	
	public void closeInputStream() throws IOException {
		this.inputStream.close();
	}
	
	public String getMimeType(ServletContext context) {		
		return (context.getMimeType(filename) == null)?	"application/octet-stream" : (context.getMimeType(filename));
	}
	
	
}

