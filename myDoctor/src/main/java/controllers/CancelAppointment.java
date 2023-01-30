package controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;
import beans.Patient;
import exceptions.DBException;
import schedule.Appointment;
import utils.ConnectionHandler;

/**
 * Servlet implementation class CancelAppointment
 */
@WebServlet("/CancelAppointment")
public class CancelAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		Appointment appointment = (Appointment) request.getAttribute("appointment");
		
		try {
			new AppointmentDAO(connection).delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
				
		response.sendRedirect("Home");		
		return;
		
		
		
	}

}
