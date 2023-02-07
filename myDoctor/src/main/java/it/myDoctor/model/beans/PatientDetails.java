package it.myDoctor.model.beans;

/**
 * PatientDetails contains contact information about the {@link Patient}
 * 
 */
public class PatientDetails {

	private int id;
	private int patientId;
	private String name;
	private String address;
	private String phone;

	/**
	 * Constructs a PatientDetails object.
	 * 
	 * @param id the id of the database record
	 * @param patientId the id of the patient
	 * @param name the name of the patient
	 * @param address the address of the patient
	 * @param phone the phone number of the patient
	 */
	public PatientDetails(int id, int patientId, String name, String address, String phone) {
		super();
		this.id = id;
		this.patientId = patientId;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}

	/*
	 * Getters & Setters
	 */
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
