package it.myDoctor.controller.filters.authorisation;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.myDoctor.controller.strategy.RoleStrategy;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Patient;

/**
 * 
 * The PatientFilter class is a Servlet filter that is used to restrict access
 * to certain URLs based on the role <b>Patient</b>
 * 
 * <p>
 * If a user is not logged in, they will receive the error message <b>'403 -
 * Forbidden'</b>
 * </p>
 * 
 * @author Diego Torlone
 * 
 */
@WebFilter(urlPatterns = { "/Agenda", "/PickAppointment", "/CancelAppointment" })

public class PatientFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	/**
	 * Overrides the doFilter method of the Filter interface to restrict access to
	 * the specified URLs based on the user's role.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*
		 * Casts the ServletRequest to an HttpServletRequest and ServletResponse to an
		 * HttpServletResponse to access the session data.
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		/*
		 * Retrieves objects from the session.
		 */
		Patient patient = (Patient) httpRequest.getSession().getAttribute("patient");
		Doctor doctor = (Doctor) httpRequest.getSession().getAttribute("medico");
		RoleStrategy strategy = (RoleStrategy) session.getAttribute("roleStrategy");

		//Evaluates if the user is a Patient 
		boolean isPatient = !session.isNew() && doctor != null && patient != null && strategy != null;

		//If the user is not a Patient, the response is sent with a 403 error code and a message
		if (!isPatient) {
			httpResponse.sendError(403, "Il tuo ruolo non permette di eseguire questa operazione");
			return;
		}

		//If the user is a Doctor, the request is passed along the filter chain
		chain.doFilter(request, response);
	}

}
