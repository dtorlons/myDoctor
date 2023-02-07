package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.exceptions.InsertionException;
import it.myDoctor.controller.exceptions.UpdateException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.AppointmentDAO;
import it.myDoctor.model.DAO.NotificationDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Notification;


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
