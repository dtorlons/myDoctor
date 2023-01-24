package beans;

public class Doctor implements User{
	
	private int id;
	private String username;
	private String password;
	private DoctorDetails doctorDetails;
	
	public Doctor(int id, String username, String password, DoctorDetails doctorDetails) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.doctorDetails = doctorDetails;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
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

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setDoctorDetails(DoctorDetails doctorDetails) {
		this.doctorDetails = doctorDetails;
	}
	
	public DoctorDetails getDoctorDetails() {
		return this.doctorDetails;
	}
	
	
	
	

}
