package schedule.beans;

import java.sql.Connection;



import DAO.NotificationDAO;
import beans.Doctor;


import exceptions.DBException;



/**
 * Used to apply the 'Observer' pattern, this class extends Doctor and implements Observer.
 * Therefore, an ObserverDoctor is a subscriber
 */
public class DoctorObserver extends Doctor implements Observer{

	private Connection connection;
	
	
	public DoctorObserver(Doctor doctor) {
		super(doctor.getId(), doctor.getUsername(), doctor.getPassword(), doctor.getDoctorDetails());		
	}
	
	//See interface Object or drag mouse over method prototype for info
	public void setConnection(Connection connection) {
		this.connection = connection;
	}	
	
	//See interface Object or drag mouse over method prototype for info
	public void update(String notification) throws DBException {		
		new NotificationDAO(connection).post(this, notification);;
	}
	
	

}
