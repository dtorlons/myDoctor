package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import beans.Patient;


public class Toolkit {		
	
	public static Blob partToBlob(Part part) throws IOException, SerialException, SQLException {		
		
		if(part == null) return null;
		
		InputStream inputStream = part.getInputStream();
		byte [] fileBytes = inputStream.readAllBytes();		
		Blob blob = new SerialBlob(fileBytes);		
		return blob;		
	}
	
	
	public static Patient findPatientById(List<Patient> patients, Integer id) {		
		return patients.stream().filter(user -> user.getId()==id).findFirst().orElse(null);
	}
	
	

}


	

