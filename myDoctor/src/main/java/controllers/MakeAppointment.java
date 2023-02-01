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
import beans.Timeband;
import exceptions.DBException;
import exceptions.InsertionException;
import utils.ConnectionHandler;


/**
 * This Servlet is called when a Doctor inserts an appointment 
 * @author diego
 *
 */
@WebServlet("/MakeAppointment")
public class MakeAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * Initialise connection to the database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Retrieve session attributes
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		
		//Retrieve appointment to be inserted and target timeband
		Appointment appointment = (Appointment) request.getAttribute("appointment");
		Timeband timeband = (Timeband) request.getAttribute("timeband");

		
		//Insert appointment
		try {
			new AppointmentDAO(connection).insert(appointment, timeband);
		} catch (DBException e) {
			response.sendError(500, e.getMessage());
			return;
		} catch (InsertionException e) {
			response.sendError(400, e.getMessage());
			return;
		}


		/*
		 * Send a notification over to the Patient related to the appointment 
		 */

		String notificationText = doctor.getDoctorDetails().getName() + " ti ha dato un appuntamento per "
				+ appointment.getFormattedDate() + " alle " + appointment.getFormattedStartTime() + ". Controlla la tua agenda per ulteriori dettagli";

		
		Notification notification = new Notification(notificationText);

		//Insert the notification
		try {
			new NotificationDAO(connection).insert(notification, appointment.getPaziente());
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (InsertionException e) {
			response.sendError(500, "Impossibile inserire notifica");
			return;
		}


		//Redirect to Agenda Management
		String data = appointment.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data=" + data;

		response.sendRedirect(path);

		return;

	}

}
