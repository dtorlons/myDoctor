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
public class Doctor extends User {

	private DoctorDetails doctorDetails;

	/**
	 * 
	 * <p>
	 * The constructor creates a new doctor instance with the given id, username,
	 * password, and DoctorDetails.
	 * </p>
	 * 
	 * @param id            The id of the doctor
	 * @param username      The username of the doctor
	 * @param password      The password of the doctor
	 * @param doctorDetails The doctor details for the doctor
	 */
	public Doctor(int id, String username, String password, DoctorDetails doctorDetails) {
		super(id, username, password);
		this.doctorDetails = doctorDetails;
	}

	/*
	 * Getters & Setters
	 */
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
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

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	public void setDoctorDetails(DoctorDetails doctorDetails) {
		this.doctorDetails = doctorDetails;
	}

	public DoctorDetails getDoctorDetails() {
		return this.doctorDetails;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", username=" + username + ", password=" + password + ", doctorDetails="
				+ doctorDetails + "]";
	}

}
