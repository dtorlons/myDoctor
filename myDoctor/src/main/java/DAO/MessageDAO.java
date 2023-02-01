package DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Appointment;
import beans.Archive;
import beans.Message;
import beans.User;
import exceptions.DBException;

/**
 * An implementation of the interface {@link DAO} allowing to interact with the
 * <i>{@link Message}</i> entity on the database
 *
 * <p>
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on the Message table in the database.
 * </p>
 *
 * @author Diego Torlone
 *
 */
public class MessageDAO implements DAO<Message, User> {
	private Connection connection;

	/**
	 * Data Access Object to interact with the '{@link Message}' entity on the database.
	 *
	 * @param connection A connection (session) with a database
	 */
	public MessageDAO(Connection connection) {
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
	public Message get(int id)  {
		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * <p>
	 * Gets a list of all {@link Message} for a specific {@link User} .
	 * </p>
	 *
	 * @param user The {@link User} whose messages are being retrieved.
	 * @return A {@link ArrayList} of all messages for the specified patient.
	 * @throws DBException If the session with the database fails.
	 */
	@Override
	public List<Message> getAll(User user) throws DBException {

		// Prepare the query
		String query = "select * from messaggio where ((sender = ?) OR (receiver = ?)) ORDER BY timestamp asc";

		// Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Executes the query and populates an ArrayList of Messages
		ResultSet result = null;
		List<Message> messages = new ArrayList<>();

		try {
			result = ps.executeQuery();
			while (result.next()) {
				messages.add(new Message(result.getInt("idMessaggio"),
						new UserDAO(connection).get(result.getString("sender")),
						new UserDAO(connection).get(result.getString("sender")), result.getTimestamp("timestamp"),
						result.getString("message"), null, result.getString("filename")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage() + "\n" + e.getLocalizedMessage());
			}
		}
		/*
		 * Return the List of Messages
		 */
		return messages;
	}

	/**
	 * <p>
	 * Insertion of a given {@link Message} to a specific {@link User}
	 * </p>
	 *
	 * @param message  The {@link Message} to be inserted
	 * @param receiver The {@link User} associated with the message
	 * @throws DBException If interaction with the database fails
	 */
	@Override
	public void insert(Message message, User receiver) throws DBException {

		// Prepare the query
		String query = "Insert into messaggio (sender, receiver, timestamp, message, attachment, filename) "
				+ "Values (?, ?, ?, ?, ?, ?)";

		// Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, message.getSender().getUsername());
			ps.setString(2, message.getReceiver().getUsername());
			ps.setTimestamp(3, message.getTimestap());
			ps.setString(4, message.getMessage());
			ps.setBlob(5, message.getAttachment());
			ps.setString(6, message.getFilename());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Execute the query
		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}

		// Disposal of the resources
		finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		return;
	}

	/**
	 *
	 * Retrieves an {@link Archive} (attachment and filename) of a {@link Message} with given
	 * message id and asker information.
	 *
	 * @param messageId the id of the {@link Message} to retrieve the archive from
	 * @param asker     the {@link User} asking for the archive
	 * @return the {@link Archive} containing the attachment and filename of the message
	 * @throws DBException if a database error occurs
	 * @throws IOException if an I/O error occurs
	 */
	public Archive getArchive(int messageId, User asker) throws DBException, IOException {

		//Prepare the query
		String query = "select filename, attachment from messaggio where idMessaggio = ? AND (sender = ? OR receiver = ?);";

		//Set all the query values
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, messageId);
			ps.setString(2, asker.getUsername());
			ps.setString(3, asker.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}

		//Execute the query and obtain the result
		ResultSet result = null;
		Archive archive = null;
		try {
			result = ps.executeQuery();
			while (result.next()) {
				archive = new Archive(result.getBlob("attachment"), result.getString("filename"));

			}
		} catch (SQLException e) {
			throw new DBException(e);

		}

		//Disposal of the resources
		finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage() + "\n" + e.getLocalizedMessage());
			}
		}

		/*
		 * Return requested Archive
		 */
		return archive;
	}

	/**
	 * Not implemented
	 *
	 * @deprecated
	 * @throws UnsupportedOperationException always
	 */
	@Deprecated
	@Override
	public void update(Message item)  {
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
	public void delete(Message item) {
		throw new UnsupportedOperationException("Not supported");
	}

}
