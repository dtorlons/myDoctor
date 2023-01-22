package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import beans.Doctor;
import beans.Notification;
import beans.Patient;
import beans.User;
import exceptions.DBException;


/*
 * A Data Access Object to interact with the notifications
 * 
 */
public class NotificationDAO implements DAO<Notification, User>{

	private Connection connection;

	/**
	 * Data Access Object to interact with the Notifications on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public NotificationDAO(Connection connection) {
		this.connection = connection;
	}
	
	
	public List<Notification> getAll(User user) {
		return null;
	}
	
	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void insert(Notification notification, User user) {
		throw new UnsupportedOperationException();
	}		

	/**
	 * Posts a notification given as a String to the given Patient
	 * 
	 * @param paziente     The receiver of the notification
	 * @param notification The text of the notification
	 * @throws DBException
	 */
	public void post(Patient paziente, String notification) throws DBException {

		// Prepare the query
		String query = "insert into notifichePazienti (idPaziente, notifica) values(?, ?)";

		PreparedStatement ps = null;

		// Set all the values in the query
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, paziente.getId());
			ps.setString(2, notification);
		} catch (SQLException e) {
			throw new DBException(e);
		}

		// Launch the query and close the resources
		try {
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} finally {

			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return;
	}

	/**
	 * Posts a notification given as a String to the given Doctor
	 * 
	 * @param paziente     The receiver of the notification
	 * @param notification The text of the notification
	 * @throws DBException
	 */
	public void post(Doctor medico, String notifica) throws DBException {

		// Prepare the query
		String query = "insert into notifichemedici (idMedico, notifica) values (?, ?)";

		PreparedStatement ps = null;

		// Set all the values in the query
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, medico.getId());
			ps.setString(2, notifica);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		// Launch the query and close the resources
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new DBException(e);
		}

		try {
			ps.close();
		} catch (SQLException e) {
			throw new DBException(e);
		}

		return;

	}	
	
	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public Notification get(int id){
		throw new UnsupportedOperationException();
	}
	

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void update(Notification item) {
		throw new UnsupportedOperationException();		
	}
	

	/**
	 * Not implemented 
	 *
	 *@deprecated
	 * @throws UnsupportedOperationException always
	 */	
	public void delete(Notification item)  {
		throw new UnsupportedOperationException();		
	}

}
