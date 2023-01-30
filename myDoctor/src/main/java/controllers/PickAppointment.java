package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;
import DAO.TimebandDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;
import schedule.Appointment;
import schedule.Timeband;
import utils.ConnectionHandler;

@WebServlet("/PickAppointment")
public class PickAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Appointment appointment = (Appointment) request.getAttribute("appointment");
		Timeband timeband = (Timeband) request.getAttribute("timeband");

		// Inserimento appuntamento
		try {
			new AppointmentDAO(connection).insert(appointment, timeband);
		} catch (DBException e) {
			response.sendError(500, e.getMessage());
			return;
		} catch (InsertionException e) {
			response.sendError(400, e.getMessage());
			return;
		}

		response.sendRedirect("Home");		
		return;
		
	}

}
