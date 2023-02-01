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
import exceptions.UpdateException;
import utils.ConnectionHandler;


/**
 * This Servlet is called upon an Appointment Update request from a Doctor
 *  
 * @author Diego Torlone
 *
 */
@WebServlet("/ModifyAppointment")
public class ModifyAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/*
	 * Initialise connection to the database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//Retrieve doctor from session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		
		//Retrieve appointment to be modified 
		Appointment appointment = (Appointment) request.getAttribute("appointment");

		
		//Update the appointment
		try {
			new AppointmentDAO(connection).update(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore nel database");
			return;
		} catch (UpdateException e) {
			response.sendError(400, e.getMessage());
			return;
		}

		/**
		 * Send a notification over to the related patient about the changes
		 */
		String notificationText = doctor.getDoctorDetails().getName() + " ha modificato l'appuntamento di "
				+ appointment.getFormattedDate() + " alle " + appointment.getFormattedStartTime() + ". Contattalo al "
				+ doctor.getDoctorDetails().getPhone() + " per ulteriori dettagli.";

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


		//Redirect to Doctor's agenda management
		String data = appointment.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data=" + data;

		response.sendRedirect(path);

		return;

	}

}
