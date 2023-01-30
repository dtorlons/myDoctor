package filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.AppointmentDAO;
import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import schedule.Appointment;
import schedule.Timeband;
import utils.ConnectionHandler;

/**
 * Servlet Filter implementation class DeleteAppointmentFilter
 */
@WebFilter("/DeleteAppointment")
public class DeleteAppointmentFilter extends HttpFilter implements Filter {
	
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}
       
    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		 
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;		
		HttpSession session = httpRequest.getSession();
		
		
		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");

		//Controllo parametri
		
		int appointmentId = -1;		
		
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
		}catch(Exception e) {			
			response.getWriter().println("Parametri errati");
			return;
		}
		
		//forse questo non è necessario
		if(appointmentId <1) {
			response.getWriter().print("Id Appuntamento non valido");
			return;
		}
		
		Appointment appointment = null;
		
		try {
			appointment = new AppointmentDAO(connection).get(appointmentId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}
		
		if (appointment == null) {
			httpResponse.sendError(400, "Appuntamento non valido");
			return;
		}
		
		
		if(appointment.getInizio().isBefore(LocalDateTime.now())) {
			httpResponse.sendError(400, "Non puoi eliminare appuntamenti nel passato");
			return;
		}
		
		Timeband timeband;
		try {
			timeband = new TimebandDAO(connection).get(appointment.getDisponibilitàId());
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}
		
		
		if(timeband.getMedico().getId() != medico.getId()) {
			response.getWriter().println("Non puoi eliminare appuntamenti che non ti appartengono");
			return;
		}
		
		
		httpRequest.setAttribute("appointment", appointment);
		httpRequest.setAttribute("timeband", timeband);
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	

}
