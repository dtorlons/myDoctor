package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.Appointment;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Doctor}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the doctor table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class DoctorDAO implements DAO<Doctor, Patient> {
	private Connection connection;

	/**
	 * Data Access Object to interact with the {@link Doctor} entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public DoctorDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a {@link Doctor} object that corresponds to a given ID from the
	 * database.
	 *
	 * @param doctorId - The identifier of the {@link Doctor} to be fetched
	 * @return The Doctor matching the given ID if it exists, <i>null</i> otherwise.
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public Doctor get(int doctorId) throws DBException {

		// Prepare the query
		String query = "select idMedico, medico.username, user.password from medico join user on (medico.username = user.username) where idMedico = ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, doctorId);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Execute the query and obtain a value
		ResultSet result = null;
		Doctor medico = null;
		try {
			result = ps.executeQuery();
			while (result.next()) {
				medico = new Doctor(result.getInt("idMedico"), result.getString("username"),
						result.getString("password"), new DoctorDetailsDAO(connection).get(doctorId));
			}
		} catch (SQLException e) {
			throw new DBException(e);

		}
		// Disposal of the resources
		finally {
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
	 *
	 * Check credentials of a {@link Doctor}
	 *
	 * @param username a <i>String </i> specifying the doctor's username
	 * @param password a <i>String </i> specifying the doctor's password
	 * @return the {@link Doctor} object with the matching credentials
	 * @throws DBException if the session with the database fails
	 */
	public Doctor checkCredentials(String username, String password) throws DBException {

		//Prepare the query
		String query = "select idMedico, user.username,  password from user join medico on user.username = medico.username where user.username = ? AND user.password = ?";

		PreparedStatement ps = null;

		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Execute the query and obtain a value
		ResultSet result = null;
		Doctor doctor = null;
		try {
			result = ps.executeQuery();
			while (result.next()) {
				doctor = new Doctor(result.getInt("idMedico"), result.getString("username"),
						result.getString("password"), new DoctorDetailsDAO(connection).get(result.getInt("idMedico")));
			}
		} catch (SQLException e) {

			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block

			}
		}

		/*
		 * Returns the requested Object
		 */
		return doctor;

	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public List<Doctor> getAll(Patient ex) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void insert(Doctor item1, Patient item2)  {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(Doctor item) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void delete(Doctor item) {
		throw new UnsupportedOperationException("Not supported");
	}

}
