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

/**
 * A set of related and reusable methods designed to provide useful, general-purpose functionality.
 * 
 * @author Diego Torlone
 *
 */
public class Toolkit {

	/**
	 * Converts a {@link Part} received within an Http request into a {@link SerialBlob}
	 * 
	 * @param part - The part to be serialized
	 * @return	A serialized {@link Blob} object ready for database manipulation  
	 * @throws IOException if an Input-Output error occurs
	 * @throws SerialException if a serialization error occurs
	 * @throws SQLException - if the serialization is uncompatible with the Blob SQL model
	 */
	public static Blob partToBlob(Part part) throws IOException, SerialException, SQLException {
		
		//verifies content
		if(part == null) return null;

		//Create a Blob from an input stream
		InputStream inputStream = part.getInputStream();
		byte [] fileBytes = inputStream.readAllBytes();
		Blob blob = new SerialBlob(fileBytes);
		return blob;
	}

	/**
	 * Fetches a {@link Patient} from a list of patients matching the given ID
	 * @param patients {@link List} of {@link Patient} to fetch from
	 * @param id an <i>Integer</i> specifying the requested patient's ID
	 * @return a {@link Patient} matching the given ID, <i>null</i> otherwise;
	 * 
	 */
	public static Patient findPatientById(List<Patient> patients, Integer id) {
		
		//Use a java filter to fetch a Patient object under a given predicate
		return patients.stream().filter(user -> user.getId()==id).findFirst().orElse(null);
	}



}




