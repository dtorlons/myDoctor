package beans;

public class Doctor extends User{
	
	
	private DoctorDetails doctorDetails;
	
	public Doctor(int id, String username, String password, DoctorDetails doctorDetails) {
		super(id, username, password);		
		this.doctorDetails = doctorDetails;
	}

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
	
	
	
	

}
