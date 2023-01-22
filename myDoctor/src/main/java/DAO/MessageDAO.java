package DAO;

import java.util.List;

import beans.Message;
import beans.User;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

public class MessageDAO implements DAO<Message, User>{

	/**
	 * @deprecated
	 */
	public Message get(int id) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> getAll(User ex) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(Message item1, User item2) throws DBException, InsertionException {
		
		
	}

	/**
	 * @deprecated
	 */
	public void update(Message item) throws DBException, UpdateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Message item) throws DBException {
		// TODO Auto-generated method stub
		
	}

}
