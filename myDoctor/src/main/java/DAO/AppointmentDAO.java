package DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;
import schedule.entities.Appointment;
import schedule.entities.Timeband;

/**°
 * An implementation of the interface {@link DAO}
 * Specific for the entity Appointment 
 */
public class AppointmentDAO implements DAO<Appointment, Timeband>{

	private Connection connection;

	/**
	 * Data Access Object to interact with the 'Appointment' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public AppointmentDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns a List of Appointments belonging to a Timeband
	 * <p>
	 * This method returns <i>null</i> if a DB access error occurs; returns the
	 * requested list otherwise
	 * </p>
	 * 
	 * @param timeband the Timeband object the Appointments in the returning list
	 *                 refer to
	 * @return a list of Appointments relative to the given timeband.
	 * @throws DBException if the session with the database fails
	 */	
	public List<Appointment> getAll(Timeband timeband) throws DBException {

		String query = "SELECT * from appuntamento where idDisponibilita = ? order by inizio asc";

		/*
		 * Prepares an SQL statement upon the given query.
		 */
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timeband.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		/*
		 * Executes the query and populates the requested ArrayList of Appointments
		 */

		ResultSet result = null;
		List<Appointment> appuntamenti = new ArrayList<>();
		try {
			result = ps.executeQuery();

			while (result.next()) {
				Appointment a = new Appointment(result.getInt("idAppuntamento"), result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new PatientDAO(connection).get(result.getInt("idPaziente")),
						result.getString("note"));
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
	 * <p>Insertion of a given appointment in a specific timeband</p>
	 * 
	 * @param Appointment The appointment to be inserted
	 * @param Timeband The Timeband the Appointment has to be inserted to
	 * @throws DBException If interaction with the database fails
	 * @throws InsertionException If the business rules regarding the operation are not adhered to
	 */
	public void insert(Appointment appointment, Timeband timeband) throws DBException, InsertionException {
		
		//Prepare the query
		String query = "CALL InserimentoAppuntamenti(?, ?, ?, ?, ?, ?, ?)";
		
		CallableStatement ps = null;

		//Set all the query values
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
				
		//Execute the query and obtain an outcome, then close all the resources
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
		
		//Propagate business rule violation to the user
		if(!outcome.equals("OK")) {
			throw new InsertionException(outcome);
		}				
		
		return;			
	}
	
	/**
	 * <p>Deletes the Appointment entry in the database</p>
	 * 
	 * @param Appointment The appointment to be deleted
	 * @throws DBException if the session with the database fails
	 *
	 */
	public void delete(Appointment appointment) throws DBException {

		//Prepare the query
		String query = "delete from appuntamento where idAppuntamento = ?";

		PreparedStatement ps = null;
		
		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, appointment.getId());
		} catch (SQLException e) {
			throw new DBException(e);			
		}
		//Execute the query,  then close all the resources
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new DBException(e);
		}

		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new DBException(e);
			}
		}

		return;
	}

	/**
	 * 	Returns an Appointment object corresponding to the identifier in the parameter
	 * 
	 * @return An Appointment with a specific id
	 * @throws DBException if the session with the database fails
	 */
	public Appointment get(int appointmentId) throws DBException {

		//Prepare the query
		String query = "Select * from appuntamento where idAppuntamento = ?";

		PreparedStatement ps = null;

		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, appointmentId);
		} catch (SQLException e) {			
			throw new DBException(e);
		}

		//Execute the query, obtain a value, then close all the resources
		ResultSet result = null;
		Appointment appointment = null;

		try {
			result = ps.executeQuery();
			while (result.next()) {
				appointment = new Appointment(result.getInt("idAppuntamento"), result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new PatientDAO(connection).get(result.getInt("idPaziente")),
						result.getString("note"));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
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
	 * Updates the Appointment object given as a parameter
	 * 
	 * @throws DBException if the session with the database fails
	 * @throws UpdateException in case the business rule regarding the operation is not adhered to
	 */
	public void update(Appointment appointment) throws DBException, UpdateException {
		
		//Prepare the query
		String query = "CALL UpdateAppuntamento (?, ?, ?, ?, ?, ?)";
		
		CallableStatement ps = null;
		
		//Set all the query values
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
		
		//Execute the query, obtain an outcome, then close all the resources
		String outcome = null;
		try {
			ps.execute();
			outcome = ps.getString(6);
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
		
		
		if(!outcome.equals("OK")) {
			throw new UpdateException(outcome);
		}
		
		return;
		
	}

}
