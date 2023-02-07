package it.myDoctor.controller.filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.TimebandDAO;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Timeband;

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
@WebFilter("/ModifyTimeband")
public class ModifyTimebandFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;
	private Connection connection;

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

		int timebandId = -1;
		LocalDate date = null;
		LocalTime begin = null;
		LocalTime end = null;
		int length = 0;

		// retrieve the timebandId from the request parameters
		try {
			timebandId = Integer.parseInt(request.getParameter("timebandId"));
			date = LocalDate.parse(request.getParameter("data"));
			begin = LocalTime.parse(request.getParameter("inizio"));
			end = LocalTime.parse(request.getParameter("fine"));
			length = Integer.parseInt(request.getParameter("minutes"));
		} catch (Exception e) {
			httpResponse.sendError(400, "Errore nei parametri");
			return;
		}

		//Validate the given parameters
		if (timebandId < 1 || length < 5 || length > 60 || begin == null || end == null || date == null) {
			httpResponse.sendError(400, "Parametri non validi");
			return;
		}
		if (LocalDateTime.of(date, begin).isBefore(LocalDateTime.now())) {
			httpResponse.sendError(400, "Non si possono apportare modifiche ad eventi del passato");
			return;
		}

		// use the TimebandDAO to get the corresponding Appointment object from the
		// database
		final TimebandDAO timebandDao = new TimebandDAO(connection);

		Timeband timeband;
		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}

		if (timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(400, "Non ha i privilegi");
			return;
		}
		
		//create the modified timeband
		timeband.setInizio(LocalDateTime.of(date, begin));
		timeband.setFine(LocalDateTime.of(date, end));

		
		//Pass the request along the chain to the Servlet
		httpRequest.setAttribute("timeband", timeband);
		chain.doFilter(request, response);
	}

}
