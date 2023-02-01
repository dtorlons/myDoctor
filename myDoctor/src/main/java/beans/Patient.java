package beans;

/**
 * 
 * <p>
 * The Doctor class extends the {@link User} class and represents a doctor in the
 * system.
 * </p>
 * 
 * @author Diego Torlone
 * 
 * @see User
 */
public class Patient extends User{


	private int idMedico;
	private PatientDetails patientDetails;

	/**
	 * 
	 * <p>
	 * The constructor creates a new Patient instance with the given id, username,
	 * password, and DoctorDetails.
	 * </p>
	 * 
	 * @param id            The id of the doctor
	 * @param username      The username of the doctor
	 * @param idMedico		The Id of the doctor associated with the patient
	 * @param password      The password of the doctor
	 * @param patientDetails The details for the patient
	 */
	public Patient(int id, String username, int idMedico, String password, PatientDetails patientDetails) {
		super(id, username, password);
		this.idMedico = idMedico;
		this.patientDetails = patientDetails;
	}


	/*
	 * Getters & Setters
	 */
	public PatientDetails getPatientDetails() {
		return patientDetails;
	}


	public void setPatientDetails(PatientDetails patientDetails) {
		this.patientDetails = patientDetails;
	}


	@Override
	public int getId() {
		return id;
	}
	@Override
	public String getUsername() {
		return username;
	}

	public int getIdMedico() {
		return idMedico;
	}
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	public void setIdMedico(int idMedico) {
		this.idMedico = idMedico;
	}
	@Override
	public void setPassword(String password) {
		this.password = password;
	}



}
