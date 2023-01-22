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

import beans.Doctor;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

import schedule.entities.Timeband;

/**°
 * An implementation of the interface {@link DAO}
 * Specific for the entity Timeband 
 */
public class TimebandDAO implements DAO<Timeband, Doctor> {

	private final Connection connection;

	/**
	 * Data Access Object to interact with the 'Timeband' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public TimebandDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * 	Returns a Timeband object corresponding to the identifier in the parameter
	 * 
	 * @return A timeband with a specific id
	 * @throws DBException if the session with the database fails
	 */
	public Timeband get(int timebandId) throws DBException {

		//Prepare the query
		String query = "select * from disponibilita where idDisponibilita = ?";

		PreparedStatement ps = null;

		//Set all the parameters
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timebandId);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		//Launch the query, obtain the results and close the resources
		ResultSet result = null;
		Timeband timeband = null;

		try {
			result = ps.executeQuery();
			while (result.next()) {
				timeband = new Timeband(result.getInt("idDisponibilita"),
						result.getTimestamp("inizio").toLocalDateTime(), result.getTimestamp("fine").toLocalDateTime(),
						new DoctorDAO(connection).get(result.getInt("idMedico")),
						result.getInt("durataStandard"));
			}
		} catch (SQLException e) {
			throw new DBException(e);

		} finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		/*
		 *	Return a timeband 
		 */
		return timeband;
	}

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public List<Timeband> getAll(Doctor ex) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a List of Timebands belonging to a Day of a specific Doctor
	 * <p>
	 * This method returns <i>null</i> if a DB access error occurs; returns the
	 * requested list otherwise
	 * </p>
	 * 
	 * @param date The LocalDate object indicating the requested Day
	 * @param Doctor The Doctor in question
	 * @return a list of Appointments relative to the given timeband.
	 * @throws DBException if the session with the database fails
	 */	
	public List<Timeband> getAll(LocalDate date, Doctor doctor) throws DBException{
		
		//Prepare the query
		String query = "select * from disponibilita where idMedico = ? AND cast(inizio as date)= ?";
		
		PreparedStatement ps = null;
		
		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, doctor.getId());
			ps.setDate(2, java.sql.Date.valueOf(date));
		} catch (SQLException e) {
			throw new DBException (e);			
		}
		
		//Launch the query, compute the list, close the resources
		ResultSet result = null;
		List<Timeband> timebands = new ArrayList<>();		
		try {
			result = ps.executeQuery();
			
			while(result.next()) {
				timebands.add( new Timeband(result.getInt("idDisponibilita"),
											result.getTimestamp("inizio").toLocalDateTime(), 
											result.getTimestamp("fine").toLocalDateTime(),
											new DoctorDAO(connection).get(result.getInt("idMedico")),
											result.getInt("durataStandard"))							
						);
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
		

		//Fill up each timeband with its appointments		
		for(Timeband timeband : timebands) {
			timeband.setAppuntamenti(new AppointmentDAO(connection).getAll(timeband));
		}
		
		return timebands;
		
	}

	/**
	 * <p>Insertion of a given Timeband for a Doctor</p>
	 * 
	 * @param Tiemeband The timeband to be inserted
	 * @param Doctor The Doctor associated with the timeband
	 * @throws DBException If interaction with the database fails
	 * @throws InsertionException If the business rules regarding the operation are not adhered to
	 */
	public void insert(Timeband timeband, Doctor medico) throws DBException, InsertionException {

		//Prepare the query
		String query = "CALL InserimentoDisponibilita (?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		//Set all the query values
		try {
			ps = connection.prepareCall(query);
			ps.setDate(1, java.sql.Date.valueOf(timeband.getInizio().toLocalDate()));
			ps.setTime(2, java.sql.Time.valueOf(timeband.getInizio().toLocalTime()));
			ps.setTime(3, java.sql.Time.valueOf(timeband.getFine().toLocalTime()));
			ps.setInt(4, medico.getId());
			ps.setInt(5, timeband.getStandardAppointmentLength());
			ps.registerOutParameter(6, Types.VARCHAR);			
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		//Execute the query and obtain an outcome, then close all the resources
		String outcome = null;

		try {
			ps.execute();
			outcome = ps.getString(6);
		} catch (SQLException e) {
			throw new DBException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		//Propagate business rule violation to the user
		if (!outcome.equals("OK")) {
			throw new InsertionException(outcome);
		}
		
		return;
	}

	/**
	 * Updates the Timeband object given as a parameter
	 * 
	 * @throws DBException if the session with the database fails
	 * @throws UpdateException in case the business rule regarding the operation is not adhered to
	 */
	public void update(Timeband timeband) throws DBException, UpdateException {

		//Prepare the query
		String query = "CALL UpdateDisponibilita(?, ?, ?, ?, ?, ?)";

		CallableStatement ps = null;

		//Set all the query values
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

		//Propagate business rule violation to the user
		if (!outcome.equals("OK")) {
			throw new UpdateException(outcome);
		}

		return;

	}

	/**
	 * <p>Deletes the Timeband entry in the database <b>and all the appointments associated with it!</b></p>
	 * 
	 * @param Timeband The Timeband to be deleted
	 * @throws DBException if the session with the database fails
	 *
	 */
	public void delete(Timeband timeband) throws DBException {

		//Prepare the query
		String query = "delete from disponibilita where idDisponibilita = ?";

		PreparedStatement ps = null;

		//Set all the query values
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, timeband.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		//Execute the query,  then close all the resources
		try {
			ps.execute();
		} catch (Exception e) {
			throw new DBException(e);
		}

		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return;

	}

	

	
}
