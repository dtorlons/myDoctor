package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.User;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

public class UserDAO implements DAO<User, String>{
	
	private Connection connection;
	
	public UserDAO (Connection connection) {
		this.connection = connection;
	}	

	@Override
	public User get(int id) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public User get(String username) throws DBException {
		
		//String query = "select * from (select idPaziente as id, username, password from paziente UNION select idMedico as id, username, password from medico) as users where username = ?";
		
		String query = "select * from user where username = ?";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, username);			
		} catch (SQLException e) {			
			throw new DBException(e);
		}
		
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
		}finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());		
			}
		}	
		
		return user;
	}
	
	
	
	@Override
	public List<User> getAll(String ex) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(User item1, String item2) throws DBException, InsertionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User item) throws DBException, UpdateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(User item) throws DBException {
		// TODO Auto-generated method stub
		
	}

}
