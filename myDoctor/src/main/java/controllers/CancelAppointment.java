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
 * This Servlet is used by the patient when deleting an appointment
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/CancelAppointment")
public class CancelAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * Initializes the connection
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Retrieve doctor
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		// Retrieve appointment to be deleted
		Appointment appointment = (Appointment) request.getAttribute("appointment");

		// Delete the appointment
		try {
			new AppointmentDAO(connection).delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}

		/*
		 * Send a notification to the Doctor
		 */

		String notificationText = appointment.getPaziente().getPatientDetails().getName()
				+ " ha cancellato l'appuntamento di " + appointment.getFormattedDate() + " alle "
				+ appointment.getFormattedStartTime();

		Notification notification = new Notification(notificationText);

		try {
			new NotificationDAO(connection).insert(notification, doctor);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (InsertionException e) {
			response.sendError(500, "Impossibile inserire notifica");
			return;
		}

		// Redirect to home

		response.sendRedirect("Home");
		return;

	}

}
