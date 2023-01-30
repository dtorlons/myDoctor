package filters.validation;

import java.io.IOException;
import java.sql.Connection;

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
import beans.Patient;
import exceptions.DBException;
import schedule.Appointment;
import utils.ConnectionHandler;

/**
 * Servlet Filter implementation class CancelAppointmentFilter
 */
@WebFilter("/CancelAppointment")
public class CancelAppointmentFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;		
		HttpSession session = httpRequest.getSession();
		

		Patient patient = (Patient)httpRequest.getSession().getAttribute("patient");

		int appointmentId = -1;
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
		}catch(Exception e) {
			httpResponse.sendError(400, "Parametri errati");
			return;
		}
		
		
		AppointmentDAO appointmentDao = new AppointmentDAO(connection);
		Appointment appointment = null;
		try {
			appointment = appointmentDao.get(appointmentId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}

		// filtrato
		if (appointment == null) {
			httpResponse.sendError(400, "Parametri errati, l'appuntamento non esiste");
			return;
		}

		if (appointment.getPaziente().getId() != patient.getId()) {
			httpResponse.sendError(403, "Non possiedi privilegi per effetturare questa operazione");
			return;
		}
		
		
		httpRequest.setAttribute("appointment", appointment);
				

		chain.doFilter(request, response);
	}

}
