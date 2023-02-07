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
import it.myDoctor.model.DAO.AppointmentDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Timeband;

/**
 * This Servlet is called when a Patient picks (takes) an Appointment from the
 * Agends
 * 
 * @author diego
 *
 */
@WebServlet("/PickAppointment")
public class PickAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/*
	 * Initialises connection with the database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Retrieve appointment to be inserted
		Appointment appointment = (Appointment) request.getAttribute("appointment");
		
		//Retrieve timeband to which the appoinment is to be inserted
		Timeband timeband = (Timeband) request.getAttribute("timeband");

		//Insert the appointment
		try {
			new AppointmentDAO(connection).insert(appointment, timeband);
		} catch (DBException e) {
			response.sendError(500, e.getMessage());
			return;
		} catch (InsertionException e) {
			response.sendError(400, e.getMessage());
			return;
		}

		//redirect to home page
		response.sendRedirect("Home");
		return;

	}

}
