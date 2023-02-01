package filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.TimebandDAO;
import beans.Doctor;
import beans.Timeband;
import exceptions.DBException;
import utils.ConnectionHandler;

/**
 * Performs a validation task before the request is processed by the servlet.
 * 
 * <p>
 * The filter checks if the timeband exists, belongs to the doctor, and is not
 * in the past. If the parameters passed to the filter are incorrect, the filter
 * will respond with an error message. If the parameters are correct, the filter
 * sets the "timeband" attribute in the request and continues the filter chain.
 * </p>
 * 
 * 
 * @author Diego Torlone
 *
 */
@WebFilter("/DeleteTimeband")
public class DeleteTimebandFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * Initializes the connection handler to establish a connection with the
	 * database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*
		 * cast the ServletRequest to an HttpServletRequest and ServletResponse to an
		 * HttpServletResponse to access the session data.
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		

		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");

		// retrieve the timebandId from the request parameters
		int timebandId;
		try {
			timebandId = Integer.parseInt(request.getParameter("timebandId"));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("Parametri di tipo errato");
			return;
		}

		if (timebandId < 1) {
			response.getWriter().print("Parametri errati");
			return;
		}

		// use the TimebandDAO to get the corresponding Appointment object from the
		// database
		TimebandDAO timebandDao = new TimebandDAO(connection);
		Timeband timeband;
		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}
		//check if the timeband exists
		if (timeband == null) {
			response.getWriter().print("Fascia oraria non esistente");
			return;
		}

		// check if the timeband belongs to the doctor, and if it is not in the past.
		if (timeband.getMedico().getId() != medico.getId()) {
			response.getWriter().print("La fascia temporale non appartiene al medico richiedente");
			return;
		}
		if (timeband.getInizio().isBefore(LocalDateTime.now())) {
			response.getWriter().print("Non si può eliminare una fascia oraria nel passato");
			return;
		}

		/*
		 * If all checks pass, the filter sets the "timeband" attribute in the request
		 * and send along the request to the Servlet.
		 */
		httpRequest.setAttribute("timeband", timeband);
		chain.doFilter(request, response);
	}

}
