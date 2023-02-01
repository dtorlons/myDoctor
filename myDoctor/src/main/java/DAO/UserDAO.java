package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.Appointment;
import beans.User;
import exceptions.DBException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link User}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the User table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class UserDAO implements DAO<User, String>{

	private Connection connection;

	/**
	 * Data Access Object to interact with the '{@link User}' entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public UserDAO (Connection connection) {
		this.connection = connection;
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public User get(int id) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Returns an {@link User} object that corresponds to a given ID from the
	 * database.
	 *
	 * @param username - A <i>String</i> object specifying the {@link User} to be fetched
	 * @return The {@link User} matching the given username if it exists, <i>null</i> otherwise.
	 * @throws DBException if the session with the database fails
	 */
	public User get(String username) throws DBException {


		//Prepare the query
		String query = "select * from user where username = ?";

		//Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		//Execute the query and obtain result
		ResultSet result = null;
		User user = null;
		try {
			result = ps.executeQuery();
			while(result.next()) {
				user = new User(result.getInt("usrId"), result.getString("username"), result.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		//Disposal of the resources
		finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}

		/*
		 * Return requested user
		 */
		return user;
	}


	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public List<User> getAll(String ex)  {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void insert(User item1, String item2) {
		throw new UnsupportedOperationException("Not supported");

	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(User item)  {
		throw new UnsupportedOperationException("Not supported");

	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void delete(User item){
		throw new UnsupportedOperationException("Not supported");

	}

}
