package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * An implementation of the interface {@link DAO} Specific for the entity
 * Patient
 */
public class PatientDAO implements DAO<Patient, Doctor>{

	private final Connection connection;
	
	/**
	 * Data Access Object to interact with the 'Patient' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public PatientDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a Patient object corresponding to the identifier in the
	 * parameter
	 * 
	 * @return A Patient with a specific id
	 * @throws DBException if the session with the database fails
	 */
	public Patient get(int pazienteId) throws DBException {
		
		//Prepare the query
		String query = "select * from paziente where idPaziente = ?";
		
		PreparedStatement ps = null;
		
		//Set all the parameters in the query
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, pazienteId);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		// Launch the query, obtain a value and close the resources
		ResultSet result = null;
		Patient paziente = null;
		try {
			result = ps.executeQuery();
		
			while(result.next()) {
				paziente = new Patient(result.getInt("idPaziente"), 
										result.getString("username"), 
										result.getInt("idMedico"), 
										result.getString("password"),
										new PatientDetailsDAO(connection).get(result.getInt("idPaziente"))
						);
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
		
		try {
			result.close();
			ps.close();
		} catch (SQLException e) {
			throw new DBException(e);
		}}
		
		/*
		 * Return a Patient
		 */
		
		return paziente;
		
		
	}
	
	public Patient chechCredentials(String username, String password) throws DBException {
		
		String query = "select * from paziente where username = ? and password = ?";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		ResultSet result = null;
		Patient patient = null;
		try {
			result = ps.executeQuery();
			while(result.next()) {
				patient = new Patient(result.getInt("idPaziente"), 
						result.getString("username"), 
						result.getInt("idMedico"), 
						result.getString("password"),
						new PatientDetailsDAO(connection).get(result.getInt("idPaziente")));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				throw new DBException(e);
			}
		}
		
		return patient;
	}
	
	/**
	 * Returns a List of Patients associated to a Doctor
	 * <p>
	 * This method returns <i>null</i> if a DB access error occurs; returns the
	 * requested list otherwise
	 * </p>
	 * 
	 * @param Doctor the Doctor object the Patient in the returning list
	 *                 refer to
	 * @return a list of Appointments relative to the given timeband.
	 * @throws DBException if the session with the database fails
	 */	
	public List<Patient> getAll(Doctor doctor) throws DBException {

		//Prepare the query
		String query = "select * from paziente where idMedico = ?";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, doctor.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		ResultSet result = null;		
		List<Patient> patients = new ArrayList<>();
		
		//Launch the query, obtain the results, close the resources
		try {	
			result = ps.executeQuery();
			while(result.next()) {
				patients.add(new Patient(	result.getInt("idPaziente"),
											result.getString("username"),
											result.getInt("idMedico"),
											result.getString("password"),
											new PatientDetailsDAO(connection).get(result.getInt("idPaziente"))));
			}
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
		 * Return a list of Patient
		 */
		return patients;
		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void insert(Patient item1, Doctor item2) throws DBException, InsertionException {
		throw new UnsupportedOperationException();		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void update(Patient item) throws DBException, UpdateException {
		throw new UnsupportedOperationException();		
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void delete(Patient item) {
		throw new UnsupportedOperationException();		
	}

		
	
}
