package it.myDoctor.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.exceptions.InsertionException;
import it.myDoctor.controller.exceptions.UpdateException;
import it.myDoctor.model.beans.Notification;
import it.myDoctor.model.beans.User;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Notification}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the Notification table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class NotificationDAO implements DAO<Notification, User> {

	private Connection connection;

	/**
	 * Data Access Object to interact with the {@link Notification} on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public NotificationDAO(Connection connection) {
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
	public Notification get(int id) {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * <p>
	 * Gets a list of all {@link Notification} for a specific {@link User} .
	 * </p>
	 *
	 * @param user The {@link user} whose notifications are being retrieved.
	 * @return A {@link ArrayList} of all notifications for the specified user.
	 * @throws DBException If the session with the database fails.
	 */
	@Override
	public List<Notification> getAll(User user) throws DBException {

		// Prepare the query
		String query = "select * from notifica where username = ? order by timestamp asc";

		// Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Executes the query and populates the ArrayList of Notifications
		ResultSet result = null;
		List<Notification> notifications = new ArrayList<>();
		try {
			result = ps.executeQuery();
			while (result.next()) {// int idNotifica, String username, String text, Timestamp timestamp
				notifications.add(new Notification(result.getInt("idNotifica"), result.getString("username"),
						result.getString("notifica"), result.getTimestamp("timestamp")));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Return the list of notifications
		 */
		return notifications;
	}

	/**
	 * <p>
	 * Insertion of a given {@link Notification} to a specific {@link User}
	 * </p>
	 *
	 * @param notification The {@link Notification} to be inserted
	 * @param recipient    The {@link User} associated with the notification
	 * @throws DBException        If interaction with the database fails
	 * @throws InsertionException If a business rule violation occurs during the
	 *                            insertion
	 */
	@Override
	public void insert(Notification notification, User recipient) throws DBException, InsertionException {

		// Prepare the query
		String query = "insert into notifica (username, notifica, timestamp) values (?, ?, ?)";

		// Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, recipient.getUsername());
			ps.setString(2, notification.getText());
			ps.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Executes the query
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new InsertionException(e.getMessage());
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return;

	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(Notification item) throws DBException, UpdateException {
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
	public void delete(Notification item) {
		throw new UnsupportedOperationException("Not supported");

	}

	/**
	 *
	 * Deletes all {@link Notification} associated with a {@link User}
	 *
	 * @param user the {@link User} whose notifications are to be deleted
	 * @throws DBException if a database error occurs
	 */
	public void deleteAll(User user) throws DBException {

		String query = "delete from notifica where username = ?";

		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		try {
			ps.execute();
		} catch (SQLException e) {
			throw new DBException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return;

	}

}
