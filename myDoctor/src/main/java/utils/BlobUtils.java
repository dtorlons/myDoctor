package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

public class BlobUtils {
	
	private static Archive archive;
	
	public static Blob partToBlob(Part part) throws IOException, SerialException, SQLException {		
		
		if(part == null) return null;
		
		InputStream inputStream = part.getInputStream();
		byte [] fileBytes = inputStream.readAllBytes();		
		Blob blob = new SerialBlob(fileBytes);		
		return blob;		
	}
	
	public static Archive getArchive(Blob blob, String filename) throws SQLException, IOException {
				
		if(archive == null) {
			archive = new Archive(blob, filename);
		}
		
		return archive;		
		
	}

}


	

