package DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import beans.Appointment;
import beans.Patient;
import beans.Timeband;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Appointment}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the Appointment table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class AppointmentDAO implements DAO<Appointment, Timeband> {

	private Connection connection;

	/**
	 * Data Access Object to interact with the {@link Appointment} entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public AppointmentDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * <p>
	 * Deletes a given appointment from the database.
	 * </p>
	 *
	 * @param appointment The appointment to be deleted
	 * @throws DBException if the session with the database fails
	 *
	 */
	@Override
	public void delete(Appointment appointment) throws DBException {

		// Prepare the query
		String query = "delete from appuntamento where idAppuntamento = ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, appointment.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		// Execute the query
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new DBException(e);
			}
		}

		// Return the record
		return;
	}

	/**
	 * Returns an appointment object that corresponds to a given ID from the
	 * database.
	 *
	 * @param appointmentId - The identifier of the Appointment to be fetched
	 * @return The appointment matching the given ID if it exists, <i>null</i>
	 *         otherwise.
	 * @throws DBException if the session with the database fails
	 */
	@Override
	public Appointment get(int appointmentId) throws DBException {

		// Prepare the query
		String query = "Select * from appuntamento where idAppuntamento = ?";

		PreparedStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, appointmentId);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Executes the query and obtains a result
		 */
		ResultSet result = null;
		Appointment appointment = null;

		try {
			result = ps.executeQuery();
			while (result.next()) {
				appointment = new Appointment(result.getInt("idAppuntamento"), result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new PatientDAO(connection).get(result.getInt("idPaziente")), result.getString("note"));
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
		/*
		 * Returns the requested Appointment
		 */
		return appointment;
	}

	/**
	 * <p>
	 * Gets a list of all appointments for a specific {@link Patient} .
	 * </p>
	 *
	 * @param patient The {@link Patient} whose appointments are being retrieved.
	 * @return A {@link ArrayList} of all appointments for the specified patient.
	 * @throws DBException If the session with the database fails.
	 */
	public List<Appointment> getAll(Patient patient) throws DBException {

		String query = "Select * from appuntamento where idPaziente = ? order by inizio desc";

		/*
		 * Prepares an SQL statement upon the given query.
		 */
		PreparedStatement ps = null;

		try {
			// Set all the query values
			ps = connection.prepareStatement(query);
			ps.setInt(1, patient.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Executes the query and populates the requested ArrayList of Appointments
		 */

		ResultSet result = null;
		List<Appointment> appointments = new ArrayList<>();
		try {
			result = ps.executeQuery();
			while (result.next()) {
				appointments.add(new Appointment(result.getInt("idAppuntamento"), result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new PatientDAO(connection).get(result.getInt("idPaziente")), result.getString("note")));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		} finally {
			/*
			 * Disposal of the resources
			 */
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		/*
		 * Return ArrayList<Appointment>
		 */
		return appointments;
	}




	/**
	 * Returns a List of all appointments that belong to the provided timeband.
	 *
	 * @param timeband the {@link Timeband} to retrieve appointments for
	 * @return a List of Appointments belonging to the provided timeband
	 * @throws DBException If the session with the database fails
	 *
	 */
	@Override
	public List<Appointment> getAll(Timeband timeband) throws DBException {

		String query = "SELECT * from appuntamento where idDisponibilita = ? order by inizio asc";

		/*
		 * Prepares an SQL statement upon the given query.
		 */
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timeband.getId());
			System.out.println("AppointmentDAO[getAll]\n" + ps.toString());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Executes the query and populates the ArrayList of Appointments
		 */
		ResultSet result = null;
		List<Appointment> appuntamenti = new ArrayList<>();
		try {
			result = ps.executeQuery();

			while (result.next()) {
				Appointment a = new Appointment(result.getInt("idAppuntamento"), result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new PatientDAO(connection).get(result.getInt("idPaziente")), result.getString("note"));
				appuntamenti.add(a); // Addition to the List
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Disposal of the used resources
		 */
		finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println("Error closing resources at <getAppuntamentobyDisponibilita>");
			}
		}

		/*
		 * Returns an ArrayList
		 */

		return appuntamenti;
	}

	/**
	 * Returns a List of all <u>free</u> appointments that belong to the provided timeband.
	 *
	 * @param timeband the {@link Timeband} to retrieve appointments for
	 * @return a List of <u>free</u> Appointments belonging to the provided timeband
	 * @throws DBException If the session with the database fails
	 *
	 */
	public List<Appointment> getAllFree(Timeband timeband) throws DBException {

		// Prepare the query
		String query = "call TabellaAppuntamentiLiberi(?, ?, ?, ?, ?)";

		// Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timeband.getId());
			ps.setDate(2, java.sql.Date.valueOf(timeband.getInizio().toLocalDate()));
			ps.setTime(3, java.sql.Time.valueOf(timeband.getInizio().toLocalTime()));
			ps.setTime(4, java.sql.Time.valueOf(timeband.getFine().toLocalTime()));
			ps.setInt(5, timeband.getStandardAppointmentLength());
		} catch (SQLException e1) {
			throw new DBException(e1);
		}

		// Launch the query, populate Appointment list
		ResultSet result = null;
		List<Appointment> freeAppointments = new ArrayList<>();
		try {
			result = ps.executeQuery();
			while (result.next()) {
				freeAppointments.add(new Appointment(result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						result.getString("note")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
		 * Return the list of Free Appointments
		 */

		return freeAppointments;

	}

	/**
	 * <p>
	 * Insertion of a given appointment in a specific {@link Timeband}
	 * </p>
	 *
	 * @param appointment The {@link Appointment} to be inserted
	 * @param timeband    The {@link Timeband} associated with the appointment
	 * @throws DBException        If interaction with the database fails
	 * @throws InsertionException If a business rule violation occurs during the insertion
	 */
	@Override
	public void insert(Appointment appointment, Timeband timeband) throws DBException, InsertionException {

		// Prepare the query
		String query = "CALL InserimentoAppuntamenti(?, ?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareCall(query);
			ps.setDate(1, java.sql.Date.valueOf(appointment.getInizio().toLocalDate()));
			ps.setTime(2, java.sql.Time.valueOf(appointment.getInizio().toLocalTime()));
			ps.setTime(3, java.sql.Time.valueOf(appointment.getFine().toLocalTime()));
			ps.setInt(4, appointment.getPaziente().getId());
			ps.setInt(5, timeband.getId());
			ps.setInt(6, timeband.getMedico().getId());
			ps.registerOutParameter(7, Types.VARCHAR);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Execute the query and obtain an outcome, then close all the resources
		String outcome = null;
		try {
			ps.execute();
			outcome = ps.getString(7);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		} finally {
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
	 * Updates an existing {@link Appointment} in the database.
	 *
	 *	@param appointment The updated appointment object.
	 * @throws DBException     if the session with the database fails
	 * @throws UpdateException If a business rule violation occurs
	 */
	@Override
	public void update(Appointment appointment) throws DBException, UpdateException {

		// Prepare the query
		String query = "CALL UpdateAppuntamento (?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		// Set all the query values
		try {
			ps = connection.prepareCall(query);
			ps.setDate(1, java.sql.Date.valueOf(appointment.getInizio().toLocalDate()));
			ps.setTime(2, java.sql.Time.valueOf(appointment.getInizio().toLocalTime()));
			ps.setTime(3, java.sql.Time.valueOf(appointment.getFine().toLocalTime()));
			ps.setInt(4, appointment.getDisponibilitàId());
			ps.setInt(5, appointment.getId());
			ps.registerOutParameter(6, Types.VARCHAR);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Execute the query and obtain an outcome
		String outcome = null;
		try {
			ps.execute();
			outcome = ps.getString(6);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}

		//Propagate business rule violation to Controller
		if (!outcome.equals("OK")) {
			throw new UpdateException(outcome);
		}

		return;

	}

}
