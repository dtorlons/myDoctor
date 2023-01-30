package schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


import beans.Doctor;

/**
 * A Timeband is the time of day that in which the appointments take place.
 * It can be though of as the 'working hours' of the doctor 
 *
 */
public class Timeband {

	private int id;
	private LocalDateTime inizio;
	private LocalDateTime fine;
	private DoctorObserver medico;	
	private int standardAppointmentLength;
	private List<Appointment> appuntamenti;
	private List<Appointment> freeAppointments;
	
	/**
	 * <p>Constuctor for the timeband. </p>
	 * <i>A timeband is a fixed period of time to which a number of Appointments refer to</i>
	 * <p><b>This constructor is to be used when instancing from a Data Access Object</b> </p>
	 * 
	 * @param id The ID for the timeband according to the database
	 * @param begin A LocalDateTime object for the beginning time of the Timeband
	 * @param end A LocalDateTime object for the end time of the Timeband
	 * @param doctor The Doctor the Timeband in question refers to
	 * @param standardAppointmentLength A specific time length (in minutes) for the appointments
	 */
	public Timeband(int id, LocalDateTime inizio, LocalDateTime fine, Doctor doctor, int standardAppointmentLength) {
		this.id = id;
		this.inizio = inizio;
		this.fine = fine;
		this.medico = new DoctorObserver(doctor);
		this.standardAppointmentLength = standardAppointmentLength;				
	}
		
	/**
	 * <p>Constuctor for the timeband. </p>
	 * <i>A timeband is a fixed period of time to which a number of Appointments refer to</i>
	 * <p><b>This constructor is to be used for an instance of a Timeband for which there is no ID</b> </p>
	 * 
	 * @param begin A LocalDateTime object for the beginning time of the Timeband
	 * @param end A LocalDateTime object for the end time of the Timeband
	 * @param doctor The Doctor the Timeband in question refers to
	 * @param standardAppointmentLength A specific time length (in minutes) for the appointments
	 */
	public Timeband(LocalDateTime inizio, LocalDateTime fine, Doctor medico, int standardAppointmentLength) {
		this.inizio = inizio;
		this.fine = fine;
		this.medico = new DoctorObserver(medico);
		this.standardAppointmentLength = standardAppointmentLength;	
		this.appuntamenti = null;		
	}		
	
	
	/*
	 * 	Getters & setters
	 */
	
	public int getId() {
		return id;
	}
	
	public DoctorObserver getMedico() {
		return this.medico;
	}
	
	public String getTimeband() {
		return inizio.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + fine.format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	public String getBeginTime() {
		return inizio.toLocalTime().toString();
	}
	
	public String getEndTime() {
		return fine.toLocalTime().toString();
	}
	
	public LocalDateTime getInizio() {
		return inizio;
	}

	public String getData() {
		return inizio.toLocalDate().toString();
	}
	
	public LocalDateTime getFine() {
		return fine;
	}	

	public int getStandardAppointmentLength() {
		return standardAppointmentLength;
	}

	public List<Appointment> getAppuntamenti() {
		return appuntamenti;
	}
	
	public void setInizio(LocalDateTime start) {
		this.inizio = start;
	}
	
	public void setFine(LocalDateTime end) {
		this.fine = end;
	}

	public boolean isPast() {
		return !inizio.isBefore(LocalDateTime.now());
	}
	public void setAppuntamenti(List<Appointment> appuntamenti) {
		this.appuntamenti = appuntamenti;
	}
	
	public void setFreeAppointments(List<Appointment> freeAppointments) {
		this.freeAppointments = freeAppointments;
	}
	
	public List<Appointment> getFreeAppointments(){
		return this.freeAppointments;
	}
	
	@Override
	public String toString() {
		return "Timeband [id=" + id + ", inizio=" + inizio + ", fine=" + fine + ", medico=" + medico
				+ ", standardAppointmentLength=" + standardAppointmentLength + "]";
	}
	
}
