package schedule.beans;

import java.sql.Connection;

import DAO.NotificationDAO;

import beans.Patient;
import exceptions.DBException;


public class PatientObserver extends Patient implements Observer{	
		
	private Connection connection;
	
	public PatientObserver(Patient paziente) {
		super(paziente.getId(), paziente.getUsername(),paziente.getIdMedico() , paziente.getPassword(), paziente.getPatientDetails());
	}
	
	
	public void update(String notification) throws DBException {		
		//new NotificationDAO(connection).post(this, notification);		
	}
	
	
	public void setConnection (Connection con) {
		this.connection = con;
	}
}
