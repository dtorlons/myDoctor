package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Doctor;
import beans.Notification;
import beans.Patient;
import beans.User;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;


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

	@Override
	public Notification get(int id) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Notification> getAll(User user) throws DBException {

		String query = "select * from notifica where username = ?";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		ResultSet result = null;
		List<Notification> notifications = new ArrayList<>();
					
										
		try {
			result = ps.executeQuery(); 
			while(result.next()) {//int idNotifica, String username, String text, Timestamp timestamp
				notifications.add(new Notification(result.getInt("idNotifica"),
													result.getString("username"), 
													result.getString("notifica"), 
													result.getTimestamp("timestamp")));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return notifications;	
		
		
	}

	@Override
	public void insert(Notification notification, User recipient) throws DBException, InsertionException {
		
		String query = "insert into notifica (username, notifica, timestamp) values (?, ?, CURRENT_TIMESTAMP())";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, recipient.getUsername());
			ps.setString(2, notification.getText());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new InsertionException(e.getMessage());
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		
		return;
		
		
		
		
		
		
	}

	@Override
	public void update(Notification item) throws DBException, UpdateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Notification item) throws DBException {
		// TODO Auto-generated method stub
		
	}
	
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
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return;
		
		
		
	}
	


}
