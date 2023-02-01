package controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;
import DAO.NotificationDAO;
import beans.Appointment;
import beans.Doctor;
import beans.Notification;
import exceptions.DBException;
import exceptions.InsertionException;
import utils.ConnectionHandler;

/**
 * This Servlet is called when a Doctor deletes an appointmentS
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/DeleteAppointment")
public class DeleteAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * Initialise connection to the databse
     */
    @Override
	public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Retrieve Doctor from session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		//Retrive appointment to be deleted
		Appointment appointment = (Appointment)request.getAttribute("appointment");

		//Delete the appointment 
		try {
			new AppointmentDAO(connection).delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}



		/*
		 * Send a notification to the Patient associated to the Appointment
		 */
		String notificationText = doctor.getDoctorDetails().getName()+ " ha cancellato l'appuntamento di " + appointment.getFormattedDate()+  " alle " + appointment.getFormattedStartTime() +". Contattalo al " + doctor.getDoctorDetails().getPhone() + " per ulteriori dettagli.";

		Notification notification = new Notification(notificationText);

		
		try {
			new NotificationDAO(connection).insert(notification, appointment.getPaziente());
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (InsertionException e) {
			response.sendError(500, "Impossibile inserire notifica");
			return;
		}



		/*
		 * Redirect to Agenda Management
		 */
		String data = appointment.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;
		response.sendRedirect(path);

		return;
	}

}
