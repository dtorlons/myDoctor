package it.myDoctor.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Patient;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Patient}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the patient table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class PatientDAO implements DAO<Patient, Doctor>{

	private final Connection connection;

	/**
	 * Data Access Object to interact with the '{@link Patient}' entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public PatientDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a {@link Patient} object that corresponds to a given ID from the
	 * database.
	 *
	 * @param patientId - The identifier of the {@link Patient} to be fetched
	 * @return The {@link Patient} matching the given ID if it exists, <i>null</i> otherwise.
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public Patient get(int patientId) throws DBException {

		//Prepare the query
		String query = "select idPaziente, idMedico, paziente.username, user.password from paziente join user on (paziente.username = user.username) where idPaziente = ?";

		PreparedStatement ps = null;

		//Set all the parameters in the query
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, patientId);			
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Launch the query and obtain a value
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
		}

		//Disposal of the resources
		finally {

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

	/**
	 *
	 * Check credentials of a {@link Patient}
	 *
	 * @param username a <i>String </i> specifying the patient's username
	 * @param password a <i>String </i> specifying the patient's password
	 * @return the {@link Patient} object with the matching credentials
	 * @throws DBException if the session with the database fails
	 */
	public Patient chechCredentials(String username, String password) throws DBException {

		//Prepare the query
		String query = "select idPaziente, user.username, idMedico, password from user join paziente on user.username = paziente.username where user.username = ? AND user.password = ?";

		//Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		//Executes the query and fetches the result
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
		}

		//Disposal of resources
		finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				throw new DBException(e);
			}
		}

		/*
		 * Return the requested patient
		 */
		return patient;
	}

	/**
	 * <p>
	 * Gets a list of all {@link Patient} for a specific {@link Doctor} .
	 * </p>
	 *
	 * @param doctor The {@link Doctor} whose Patients are being retrieved.
	 * @return A {@link ArrayList} of all {@link Patient} for the specified patient.
	 * @throws DBException If the session with the database fails.
	 */
	@Override
	public List<Patient> getAll(Doctor doctor) throws DBException {

		//Prepare the query
		String query = "select idPaziente, user.username, idMedico, password from paziente join user on user.username = paziente.username where idMedico = ?";

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
	@Deprecated
	@Override
	public void insert(Patient item1, Doctor item2) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(Patient item)  {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void delete(Patient item) {
		throw new UnsupportedOperationException();
	}



}
