package DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import beans.Appointment;
import beans.Doctor;
import beans.Timeband;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Timeband}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the Timeband table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class TimebandDAO implements DAO<Timeband, Doctor> {

	private final Connection connection;

	/**
	 * Data Access Object to interact with the '{@link Timeband}' entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public TimebandDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a {@link Timeband} object that corresponds to a given ID from the
	 * database.
	 *
	 * @param timebandId - The identifier of the Timeband to be fetched
	 * @return The {@link Timeband} matching the given ID if it exists, <i>null</i>
	 *         otherwise.
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public Timeband get(int timebandId) throws DBException {

		// Prepare the query
		String query = "select * from disponibilita where idDisponibilita = ?";

		PreparedStatement ps = null;

		// Set all the parameters
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timebandId);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Launch the query and obtain the results
		ResultSet result = null;
		Timeband timeband = null;

		try {
			result = ps.executeQuery();
			while (result.next()) {
				timeband = new Timeband(result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new DoctorDAO(connection).get(result.getInt("idMedico")), result.getInt("durataStandard"));
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
				e.printStackTrace();
			}
		}

		// Sets all the {@link Appointment} related to the timeband
		timeband.setAppuntamenti(new AppointmentDAO(connection).getAll(timeband));

		/*
		 * Return a timeband
		 */
		return timeband;
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public List<Timeband> getAll(Doctor ex) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 *
	 * Returns a list of {@link Timeband} for a given Doctor and LocalDate
	 *
	 * @param date   {@link LocalDate} object representing the date for which the Timebands
	 *               are requested
	 * @param doctor {@link Doctor} object representing the doctor whose Timebands are
	 *               requested
	 * @return A List of Timebands that correspond to the given doctor and LocalDate
	 * @throws DBException If the session with the database fails
	 */

	public List<Timeband> getAll(LocalDate date, Doctor doctor) throws DBException {

		// Prepare the query
		String query = "select * from disponibilita where idMedico = ? AND cast(inizio as date)= ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, doctor.getId());
			ps.setDate(2, java.sql.Date.valueOf(date));
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Launch the query, compute the list
		ResultSet result = null;
		List<Timeband> timebands = new ArrayList<>();
		try {
			result = ps.executeQuery();

			while (result.next()) {
				timebands.add(new Timeband(result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new DoctorDAO(connection).get(result.getInt("idMedico")), result.getInt("durataStandard")));
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
				System.err.println(e.getMessage());
			}
		}

		// Fill up each timeband with its appointments
		for (Timeband timeband : timebands) {
			timeband.setAppuntamenti(new AppointmentDAO(connection).getAll(timeband));
			timeband.setFreeAppointments(new AppointmentDAO(connection).getAllFree(timeband));
		}

		return timebands;

	}

	/**
	 * <p>
	 * Insertion of a given Timeband for a Doctor
	 * </p>
	 *
	 * @param timeband The {@link Timeband} to be inserted
	 * @param doctor    The {@link Doctor} associated with the timeband
	 * @throws DBException        If interaction with the database fails
	 * @throws InsertionException If the business rules regarding the operation are
	 *                            not adhered to
	 */
	@Override
	public void insert(Timeband timeband, Doctor doctor) throws DBException, InsertionException {

		// Prepare the query
		String query = "CALL InserimentoDisponibilita (?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareCall(query);
			ps.setDate(1, java.sql.Date.valueOf(timeband.getInizio().toLocalDate()));
			ps.setTime(2, java.sql.Time.valueOf(timeband.getInizio().toLocalTime()));
			ps.setTime(3, java.sql.Time.valueOf(timeband.getFine().toLocalTime()));
			ps.setInt(4, doctor.getId());
			ps.setInt(5, timeband.getStandardAppointmentLength());
			ps.registerOutParameter(6, Types.VARCHAR);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Execute the query and obtain an outcome
		String outcome = null;

		try {
			ps.execute();
			outcome = ps.getString(6);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		//Disposal of the resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}

		// Propagate business rule violation to the user
		if (!outcome.equals("OK")) {
			throw new InsertionException(outcome);
		}

		return;
	}



	/**
	 * Updates an existing {@link Timeband} in the database.
	 *
	 *	@param timeband The updated {@link Timeband} object.
	 * @throws DBException     if the session with the database fails
	 * @throws UpdateException If a business rule violation occurs
	 */
	@Override
	public void update(Timeband timeband) throws DBException, UpdateException {

		// Prepare the query
		String query = "CALL UpdateDisponibilita(?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareCall(query);
			ps.setDate(1, java.sql.Date.valueOf(timeband.getInizio().toLocalDate()));
			ps.setTime(2, java.sql.Time.valueOf(timeband.getInizio().toLocalTime()));
			ps.setTime(3, java.sql.Time.valueOf(timeband.getFine().toLocalTime()));
			ps.setInt(4, timeband.getId());
			ps.setInt(5, timeband.getMedico().getId());
			ps.registerOutParameter(6, Types.VARCHAR);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Execute the query, obtain an outcome

		String outcome = null;
		try {
			ps.execute();
			outcome = ps.getString(6);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		//Disposal of resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}

		// Propagate business rule violation to the user
		if (!outcome.equals("OK")) {
			throw new UpdateException(outcome);
		}

		return;

	}



	/**
	 * <p>
	 * Deletes a given {@link Timeband} from the database.
	 * </p>
	 *
	 * @param timeband The {@link Timeband} to be deleted
	 * @throws DBException if the session with the database fails
	 *
	 */
	@Override
	public void delete(Timeband timeband) throws DBException {

		// Prepare the query
		String query = "delete from disponibilita where idDisponibilita = ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timeband.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		// Execute the query
		try {
			ps.execute();
		} catch (Exception e) {
			throw new DBException(e);
		}

		//Disposal of the resources
		finally {
		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}}

		return;

	}

}
