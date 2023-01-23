package beans;

public class Patient implements User{

	private int id;
	private String username;
	private String password;
	private int idMedico;	
	private PatientDetails patientDetails;
	
	public Patient(int id, String username, int idMedico, String password, PatientDetails patientDetails) {
		super();
		this.id = id;
		this.username = username;
		this.idMedico = idMedico;
		this.password = password;
		this.patientDetails = patientDetails;
	}
		

	public PatientDetails getPatientDetails() {
		return patientDetails;
	}


	public void setPatientDetails(PatientDetails patientDetails) {
		this.patientDetails = patientDetails;
	}


	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public int getIdMedico() {
		return idMedico;
	}

	public String getPassword() {
		return password;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setIdMedico(int idMedico) {
		this.idMedico = idMedico;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
}
