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
import schedule.entities.Appointment;
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

		Patient self = (Patient)request.getSession().getAttribute("patient");		
		
		int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
						
		AppointmentDAO appointmentDao = new AppointmentDAO(connection);
		Appointment appointment = null;
		try {
			appointment = appointmentDao.get(appointmentId);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		if(appointment == null) {
			response.sendError(400, "Parametri errati, l'appuntamento non esiste");
			return;
		}
		
		if(appointment.getPaziente().getId() != self.getId()) {
			response.sendError(403, "Non possiedi privilegi per effetturare questa operazione");
			return;
		}
		
		try {
			appointmentDao.delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		
		String path = this.getServletContext().getContextPath() + "/Agenda";
		response.sendRedirect(path);		
		return;
		
		
		
	}

}
