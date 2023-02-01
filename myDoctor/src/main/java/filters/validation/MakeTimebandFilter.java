package filters.validation;

import java.io.IOException;
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


import beans.Doctor;
import beans.Timeband;

/**
 * Performs a validation task before the request is processed by the servlet.
 * 
 * The filter checks the validity of the parameters sent and the following conditions:
 * 
 * presence of all required parameters, valid date and time format for the time parameters,
 * if the start time is not before the current time and end time is after the start
 * time, if the duration is between 5 to 60 minutes. 
 * The filter sends error messages as response to the request 
 * if any of the above conditions are not met.
 * 
 * * <p>
 * Responds with the following HTTP Status codes:
 * <li><b>400 - Bad request </b> If the parameters provided are incorrect
 * </p>
 * <br>
 * 
 * 
 * @author Diego Torlone
 *
 */
@WebFilter("/MakeTimeband")
public class MakeTimebandFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;

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

		// retrieve request parameters
		String _data = request.getParameter("data");
		String _inizio = request.getParameter("inizio");
		String _fine = request.getParameter("fine");
		int minuti;
		
		
		//Parse request parameters
		try {
			minuti = Integer.parseInt(request.getParameter("minutes"));
		} catch (Exception e) {
			response.getWriter().println("Errore nei minuti");
			return;
		}

		if (_data == null || _inizio == null || _fine == null) {
			httpResponse.sendError(400, "Parametri vuoti");
			return;
		}

		LocalDate date = LocalDate.parse(_data);
		LocalDateTime inizio = LocalDateTime.of(date, LocalTime.parse(_inizio));
		LocalDateTime fine = LocalDateTime.of(date, LocalTime.parse(_fine));

		
		//Validate all the given parameters
		if (inizio.isBefore(LocalDateTime.now()) || fine.isBefore(inizio)) {
			httpResponse.sendError(400, "Errore nelle date");			
			return;
		}
		if (inizio.plusMinutes(5).isAfter(fine)) {
			httpResponse.sendError(400, "Durata minima 5 minuti");
			return;
		}
		if (!(minuti >= 5 && minuti <= 60)) {
			httpResponse.sendError(400, "Durata fuori dal range");			
			return;
		}

		
		//Create the timeband object
		Timeband temporaryTimeband = new Timeband(inizio, fine, medico, minuti);

		httpRequest.setAttribute("temporaryTimeband", temporaryTimeband);

		//pass the request to the Servlet
		chain.doFilter(request, response);
	}

}
