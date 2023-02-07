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
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.NotificationDAO;
import it.myDoctor.model.DAO.TimebandDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Notification;
import it.myDoctor.model.beans.Timeband;

/**
 * This Servlet is called when a Doctor deletes a Timeband
 * @author diego
 *
 */
@WebServlet("/DeleteTimeband")
public class DeleteTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * Initialises connection to the database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Retrieves Doctor from session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		
		//Retrieves Timeband to be deleted
		Timeband timeband = (Timeband) request.getAttribute("timeband");

		//Delete the timeband
		try {
			new TimebandDAO(connection).delete(timeband);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}



		/*
		 * Send a notification to *each* patient whose appointments have been cancelled
		 */

		for(Appointment appointment : timeband.getAppuntamenti()) {
			
			//Prepare the message	
			String notificationText = doctor.getDoctorDetails().getName() + " ha cancellato l'appuntamento di "
					+ appointment.getFormattedDate() + " alle " + appointment.getFormattedStartTime() + ". Contattalo al "
					+ doctor.getDoctorDetails().getPhone() + " per ulteriori dettagli.";

			
			Notification notification = new Notification(notificationText);

			try {
				//Send the message
				new NotificationDAO(connection).insert(notification, appointment.getPaziente());
			} catch (DBException e) {
				response.sendError(500, "Errore database");
				return;
			} catch (InsertionException e) {
				response.sendError(500, "Impossibile inserire notifica");
				return;
			}

		}



		/*
		 * Redirect to Agenda management
		 */

		String data = timeband.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data=" + data;

		response.sendRedirect(path);

		return;

	}

}
