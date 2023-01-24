package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * An implementation of the interface {@link DAO} Specific for the entity
 * Appointment
 */
public class DoctorDAO implements DAO<Doctor, Patient> {
	private Connection connection;
	
	/**
	 * Data Access Object to interact with the 'Doctor' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public DoctorDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a Doctor object corresponding to the identifier in the
	 * parameter
	 * 
	 * @return A Doctor with a specific id
	 * @throws DBException if the session with the database fails
	 */
	public Doctor get(int idMedico) throws DBException {

		// Prepare the query
		String query = "Select * from medico where idMedico = ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, idMedico);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Execute the query, obtain a value, then close all the resources
		ResultSet result = null;
		Doctor medico = null;
		try {
			result = ps.executeQuery();
			while (result.next()) {
				medico = new Doctor(result.getInt("idMedico"), 
									result.getString("username"),
									result.getString("password"), 
									new DoctorDetailsDAO(connection).get(idMedico));
			}
		} catch (SQLException e) {
			throw new DBException(e);

		} finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		/*
		 * Return a Doctor
		 */
				
		return medico;
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public List<Doctor> getAll(Patient ex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void insert(Doctor item1, Patient item2) throws DBException, InsertionException {
		throw new UnsupportedOperationException();}
	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void update(Doctor item) throws DBException, UpdateException {
		throw new UnsupportedOperationException();}
	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void delete(Doctor item) {
		throw new UnsupportedOperationException();}

	}
