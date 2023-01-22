package beans;

public class PatientDetails {
	
	private int id;
	private int patientId;
	private String name;
	private String address;
	private String phone;
	
	public PatientDetails(int id, int patientId, String name, String address, String phone) {
		super();
		this.id = id;
		this.patientId = patientId;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}

	public int getId() {
		return id;
	}

	public int getPatientId() {
		return patientId;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	
	
	

}
