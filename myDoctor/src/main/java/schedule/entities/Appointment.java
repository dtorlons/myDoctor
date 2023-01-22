package schedule.entities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import beans.Patient;
import schedule.beans.PatientObserver;

/**
  * This can be thought of as the formal arrangement in which Doctor and Patient meet  at a particular time. 
 *
 */
public class Appointment {

	private int id;
	private int disponibilitàId;
	private LocalDateTime inizio;
	private LocalDateTime fine;
	private PatientObserver paziente;	
	private String note;	
	
	/**
	 * Creates an instance of Appointment with the specified arguments.
	 * 
	 * @param id Unique identifier
	 * @param timebandId  The Timeband is referred to
	 * @param start A LocalDateTime object indicating the starting time of the Appointment
	 * @param end A LocalDateTime object indicating the ending time of the Appointment
	 * @param patient A Patient object linked to the Appointment
	 * @param notes A String of text.
	 */
	public Appointment(int id, int disponibilitàId, LocalDateTime inizio, LocalDateTime fine, Patient paziente, String note) {		
		this.id = id;
		this.disponibilitàId = disponibilitàId;
		this.inizio = inizio;
		this.fine = fine;
		this.paziente = new PatientObserver(paziente);
	
	}
	
	/**
	 * Creates an instance of Appointment with the specified arguments.
	 *  
	 * @param timebandId  The Timeband is referred to
	 * @param start A LocalDateTime object indicating the starting time of the Appointment
	 * @param end A LocalDateTime object indicating the ending time of the Appointment
	 * @param patient A Patient object linked to the Appointment
	 * @param notes A String of text.
	 */
	public Appointment(int disponibilitàId, LocalDateTime inizio, LocalDateTime fine, Patient paziente, String note) {				
		this.disponibilitàId = disponibilitàId;
		this.inizio = inizio;
		this.fine = fine;
		this.paziente = new PatientObserver(paziente); //TODO: Rivedi questa cosa del polimorfismo 
		this.note = note;		
	}
	
	/*
	 * Getters & Setters
	 */
	
	public PatientObserver getPaziente() {		
		return this.paziente;
	}
	
	public String getFormattedTimeband() {
		return inizio.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + fine.format(DateTimeFormatter.ofPattern("HH:mm"));		
	}
		
	public int getId() {
		return id;
	}	

	public int getDisponibilitàId() {
		return disponibilitàId;
	}

	public LocalDateTime getInizio() {
		return inizio;
	}
	
	public String getFormattedStartTime() {
		return inizio.toLocalTime().toString();
	}
	
	public String getFormattedEndTime() {
		return fine.toLocalTime().toString();
	}
	
	public String getDate() {
		return inizio.toLocalDate().toString();
		
	}

	public LocalDateTime getFine() {
		return fine;
	}	

	public String getNote() {
		return note;
	}
	
	public void setInizio(LocalDateTime inizio) {
		this.inizio = inizio;
	}
	
	public void setFine(LocalDateTime fine) {
		this.fine = fine;
	}

	

	
	
	
	
	
	
	
}
