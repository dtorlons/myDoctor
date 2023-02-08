package it.myDoctor.model.beans;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.model.DAO.TimebandDAO;

/**
 * 
 * The Day class represents a day in a schedule. 
 * <p>It may contain from 0 to many {@link Timeband}</p>
 * 
 * @author Diego Torlone
 */
public class Day {

	private LocalDate day;
	private List<Timeband> timebands;
	private Doctor medico;

	/**
	 * <p>
	 * This is the constructor for a Day in the Schedule.
	 * </p>
	 *
	 * @param day        A calendar date relative to the requested day
	 * @param medico     The requesting doctor
	 * @param connection A connection (session) with a specific database
	 * @throws DBException If there is an error with the database.
	 */
	public Day(LocalDate day, Doctor medico, Connection connection) throws DBException {
		this.day = day;
		this.medico = medico;
		this.timebands = new TimebandDAO(connection).getAll(day, medico);
	}

	/**
	 * <p>
	 * Appends the given Timeband to the list of timebands
	 * 
	 * @param timeband Timeband to be appended to the list.
	 */
	public void addDisponibilita(Timeband timeband) {
		// Appends the timeband if the timeband date matches this day.
		if (timeband.getInizio().toLocalDate().equals(day)) {
			timebands.add(timeband);
		}
	}
	
	

	/*
	 * Getters & setters
	 */

	public List<Timeband> getTimebands() {
		return this.timebands;
	}

	public String getDayOfYear() {
		return day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	public String getFormattedDay() {
		return day.format(DateTimeFormatter.ofPattern("E dd/MM/yy"));
	}

	public Doctor getDoctor() {
		return this.medico;
	}

	public boolean isPast() {
		return !day.isBefore(LocalDate.now().minusDays(1));
	}
	
	public boolean isOld() {
		return !day.isBefore(LocalDate.now());
	}

}
