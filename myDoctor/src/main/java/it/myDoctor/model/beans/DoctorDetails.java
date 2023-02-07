package it.myDoctor.model.beans;

/**
 * DoctorDetails contains contact information about the {@link Doctor}
 * 
 */
public class DoctorDetails {

	private int idAnagrafica;
	private int idMedico;
	private String name;
	private String address;
	private String phone;


	/**
	 * Constructs a DoctorDetails object.
	 * 
	 * @param idAnagrafica the id of the database record
	 * @param idMedico the id of the doctor
	 * @param name the name of the doctor
	 * @param address the address of the doctor
	 * @param phone the phone number of the doctor
	 */
	public DoctorDetails(int idAnagrafica, int idMedico, String name, String address, String phone) {
		super();
		this.idAnagrafica = idAnagrafica;
		this.idMedico = idMedico;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}


	/*
	 * Getters & Setters
	 */
	public int getIdAnagrafica() {
		return idAnagrafica;
	}


	public int getIdMedico() {
		return idMedico;
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


	public void setIdAnagrafica(int idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}


	public void setIdMedico(int idMedico) {
		this.idMedico = idMedico;
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


	@Override
	public String toString() {
		return "DoctorDetails [idAnagrafica=" + idAnagrafica + ", idMedico=" + idMedico + ", name=" + name
				+ ", address=" + address + ", phone=" + phone + "]";
	}





}
