package it.myDoctor.controller.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;

/**
 * <p>This is the constructor for the database connection handler</p>
 * This implementation works only upon a Java Servlet context and uses only one connection to the database.
 * <br>
 * <p><b>Requires some initial parameters specified web.xml:</b> </p>
 * <i> 	dbDriver: the database connector <br>
 * 		dbUrl: the url for the database <br>
 * 		dbUser: user for logging into the database <br>
 * 		dbPassword: the password associated to the user
 * </i>  <br>
 *
 * @param context The Servlet Context from which retreive the above parameters
 * @see https://www.mysql.com/products/connector/
 * 
 * @author Diego Torlone
 */
public class ConnectionHandler {

	private Connection connection = null;
	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;

	/**
	 * Constructor for a Connection Handler
	 *
	 * @param servletContext - a {@link ServletContext} to retreive the connection parameters from
	 */
	public ConnectionHandler(ServletContext servletContext) {
		this.driver = servletContext.getInitParameter("dbDriver");
		this.url = servletContext.getInitParameter("dbUrl");
		this.user = servletContext.getInitParameter("dbUser");
		this.password = servletContext.getInitParameter("dbPassword");

		//Loads the database connector driver and establishes a connection
		if (paramsCorrect()) {
			try {
				Class.forName(driver);
			} catch (Exception e) {
				System.err.println("*** Error trying to load connector driver");
				e.printStackTrace();
			}
			try {
				connection = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				System.err.println("*** Error establishing a connection with the DB");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Private method used to identify issues with the context parameters
	 * @return <i>true</i> if all the required parameters are retreivable from web.xml. False otherwise
	 */
	private boolean paramsCorrect() {
		if (driver == "" || driver == null || url == "" || url == null || user == "" || user == null || password == ""
				|| password == null) {
			System.err.println("*** web.xml file not written correctly - unable to resolve initial DB parameters");
			return false;
		}

		return true;
	}

	/**
	 * Returns a session to the database if present, <i>null</i> otherwise
	 *
	 * @return A JDBC connection to the database if existing. Returns null and prints an error message to the console otherwise
	 */
	public Connection getConnection() {

		if (connection != null) {
			return this.connection;
		}

		System.err.println("*** Null connection!");
		return null;

	}

	/**
	 * 	Disposes of the connection to the database
	 */
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("Error closing connection to DB");
			}
		}
	}

}
