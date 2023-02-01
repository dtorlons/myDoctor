package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.Appointment;
import beans.Doctor;
import beans.DoctorDetails;
import exceptions.DBException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link DoctorDetails}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the DoctorDetails table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
class DoctorDetailsDAO implements DAO<DoctorDetails, Doctor> {
	private final Connection connection;

	/**
	 * Data Access Object to interact with the '{@link DoctorDetails}' entity on the
	 * database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public DoctorDetailsDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a {@link DoctorDetails} object that corresponds to an Id of a given {@link Doctor} from the
	 * database.
	 *
	 * @param doctorId - The identifier of the DoctorDetails to be fetched
	 * @return The {@link DoctorDetails} object matching the given ID if it exists, <i>null</i>
	 *         otherwise.
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public DoctorDetails get(int doctorId) throws DBException {

		//Prepare the query
		String query = "select * from anagraficamedico where idMedico = ?";

		//Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, doctorId);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Executes the query and obtains a result
		 */
		DoctorDetails doctorDetails = null;
		ResultSet result = null;
		try {
			result = ps.executeQuery();
			while (result.next()) {
				doctorDetails = new DoctorDetails(result.getInt("idAnagrafica"), result.getInt("idMedico"),
						result.getString("nome"), result.getString("indirizzo"), result.getString("telefono"));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage() + "\n" + e.getLocalizedMessage());
			}
		}

		/*
		 * Return the requested object
		 */
		return doctorDetails;

	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public List<DoctorDetails> getAll(Doctor ex)  {
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
	public void insert(DoctorDetails item1, Doctor item2)  {
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
	public void update(DoctorDetails item) {
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
	public void delete(DoctorDetails item) {
		throw new UnsupportedOperationException("Not supported");

	}

}
