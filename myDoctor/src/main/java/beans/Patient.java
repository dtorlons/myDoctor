package beans;

public class Patient extends User{


	private int idMedico;	
	private PatientDetails patientDetails;
	
	public Patient(int id, String username, int idMedico, String password, PatientDetails patientDetails) {
		super(id, username, password);		
		this.idMedico = idMedico;
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
