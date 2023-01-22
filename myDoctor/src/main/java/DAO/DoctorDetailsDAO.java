package DAO;

import java.sql.Connection;
import java.util.List;

import beans.DoctorDetails;
import beans.Doctor;
import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;
/**
 * An implementation of the interface {@link DAO} Specific for the entity
 * DoctorDetails
 */
public class DoctorDetailsDAO implements DAO<DoctorDetails, Doctor>{
	private final Connection connection;
	
	/**
	 * Data Access Object to interact with the 'DoctorDetails' entity on the database.
	 * 
	 * @param connection A connection (session) with a database
	 */
	public DoctorDetailsDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public DoctorDetails get(int id) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DoctorDetails> getAll(Doctor ex) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(DoctorDetails item1, Doctor item2) throws DBException, InsertionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(DoctorDetails item) throws DBException, UpdateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(DoctorDetails item) throws DBException {
		// TODO Auto-generated method stub
		
	}




}
