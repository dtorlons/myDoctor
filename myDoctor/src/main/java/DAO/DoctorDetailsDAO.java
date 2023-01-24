package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		
		String query = "select * from anagraficamedico where idMedico = ?";
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, id);			
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		DoctorDetails doctorDetails = null;
		
		ResultSet result = null;
		try {
			result = ps.executeQuery();
			while(result.next()) {
				doctorDetails = new DoctorDetails(	result.getInt("idAnagrafica"),
													result.getInt("idMedico"), 
													result.getString("nome"), 
													result.getString("indirizzo"), 
													result.getString("telefono"));
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}finally {
			try {
				result.close();
				ps.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage() +"\n" +e.getLocalizedMessage());
			}
		}
				
		return doctorDetails;
		
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
