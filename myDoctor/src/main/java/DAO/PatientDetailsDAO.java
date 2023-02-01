package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.Appointment;
import beans.Patient;
import beans.PatientDetails;
import exceptions.DBException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link PatientDetails}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the PatientDetails table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class PatientDetailsDAO implements DAO<PatientDetails, Patient>{
	private final Connection connection;

	/**
	 * Data Access Object to interact with the '{@link PatientDetails}' entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public PatientDetailsDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a {@link PatientDetails} object that corresponds to a given ID from the
	 * database.
	 *
	 * @param patientId - The identifier of the PatientDetails to be fetched
	 * @return The {@link PatientDetails} object matching the given ID if it exists, <i>null</i>
	 *         otherwise.
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
		}

		//Dispose of the resources
		finally {
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
	@Deprecated
	@Override
	public List<PatientDetails> getAll(Patient ex) throws DBException {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void insert(PatientDetails item1, Patient item2) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(PatientDetails item)  {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void delete(PatientDetails item)  {
		throw new UnsupportedOperationException("Not supported");
	}

}
