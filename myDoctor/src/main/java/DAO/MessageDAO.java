package DAO;



import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Archive;
import beans.Message;
import beans.User;
import exceptions.DBException;
import exceptions.UpdateException;

public class MessageDAO implements DAO<Message, User>{
	
	private Connection connection;
	
	public MessageDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @deprecated
	 */
	public Message get(int id) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> getAll(User user) throws DBException {
		
		String query = "select * from messaggio where ((sender = ?) OR (receiver = ?)) ORDER BY timestamp asc";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getUsername());			
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		ResultSet result = null;
		List<Message> messages = new ArrayList<>();
	
		try {
			result = ps.executeQuery();
			while(result.next()) {
					messages.add(new Message(result.getInt("idMessaggio"), 
											new UserDAO(connection).get(result.getString("sender")), 
											new UserDAO(connection).get(result.getString("sender")), 
											result.getTimestamp("timestamp"), 
											result.getString("message"), 
											null, 
											result.getString("filename")));
			}
		} catch (SQLException e) {		
			e.printStackTrace();
			throw new DBException(e);
		}finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage()+"\n"+e.getLocalizedMessage());
			}
		}
				
		return messages;
	}

	@Override
	public void insert(Message message, User receiver) throws DBException {
		
		String query = "Insert into messaggio (sender, receiver, timestamp, message, attachment, filename) "
					+  "Values (?, ?, ?, ?, ?, ?)";
		
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
		
		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e);
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());			
			}
		}
		return;		
	}
	
	public Archive getArchive(int messageId, User asker) throws DBException, IOException {
		
		String query = "select filename, attachment from messaggio where idMessaggio = ? AND (sender = ? OR receiver = ?);";
		
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, messageId);
			ps.setString(2, asker.getUsername());
			ps.setString(3, asker.getUsername());
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		ResultSet result = null;
		Archive archive = null;
		try {
			result = ps.executeQuery();
			while(result.next()) {
				archive = new Archive (result.getBlob("attachment"), result.getString("filename"));
			
			}
		} catch (SQLException e) {			
			throw new DBException(e);
			
		}finally {
			try {
				ps.close();
				result.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage()+"\n"+e.getLocalizedMessage());
			}
		}
		
		return archive;		
	}

	
	
	/**
	 * @deprecated
	 */
	public void update(Message item) throws DBException, UpdateException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @deprecated
	 */
	public void delete(Message item) throws DBException {
		// TODO Auto-generated method stub
		
	}

}
