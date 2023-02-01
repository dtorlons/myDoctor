package beans;

/**
 * This class represents an user in the system
 * <p>
 * Implemented by {@link Doctor} and {@link Patient}
 * </p>
 *
 */
public class User {
	protected int id;
	protected String username;
	protected String password;

	/**
	 * Constructor for the user
	 * 
	 * @param id       Record identifier
	 * @param username a <i>String </i> specifying the user's username
	 * @param password a <i>String </i> specifying the user's password
	 */
	public User(int id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}

	
	/*
	 * Getters and setters
	 *  
	 */
	
	
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

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

}
