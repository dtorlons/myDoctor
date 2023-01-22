package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.PatientDetails;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * An implementation of the interface {@link DAO} Specific for the entity
 * PatientDetails.
 * It represents the details such as full name, address and telephone number of a Patient
 */
public class PatientDetailsDAO implements DAO<PatientDetails, Patient>{
	private final Connection connection;
	
	/**
	 * Data Access Object to interact with the 'PatientDetails' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public PatientDetailsDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Returns a PatientDetails object corresponding to the identifier in the
	 * parameter
	 * 
	 * @return The ID of the patient in question
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public PatientDetails get(int patientId) throws DBException {

		//Prepare the query
		String query = "select * from anagraficapaziente where idPaziente = ?";
		
		PreparedStatement ps = null;

		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, patientId);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		//Launch the query, obtain the values, close the resources
		ResultSet result = null;
		PatientDetails patientDetails = null;
		
		try {
			result = ps.executeQuery();
			while(result.next()) {
				patientDetails = new PatientDetails(result.getInt("idAnagrafica"), 
													result.getInt("idPaziente"), 
													result.getString("nome"), 
													result.getString("indirizzo"), 
													result.getString("telefono"));	}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		/*
		 * Return PatientDetails
		 */
		return patientDetails;		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	@Override
	public List<PatientDetails> getAll(Patient ex) throws DBException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	@Override
	public void insert(PatientDetails item1, Patient item2) throws DBException, InsertionException {
		throw new UnsupportedOperationException();
		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	@Override
	public void update(PatientDetails item) throws DBException, UpdateException {
		throw new UnsupportedOperationException();
		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	@Override
	public void delete(PatientDetails item) throws DBException {
		throw new UnsupportedOperationException();
		
	}

}
