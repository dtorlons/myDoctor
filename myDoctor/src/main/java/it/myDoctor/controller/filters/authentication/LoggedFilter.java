package it.myDoctor.controller.filters.authentication;

import java.io.IOException;
import java.util.List;

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

import it.myDoctor.controller.strategy.*;
import it.myDoctor.model.beans.*;


/**
 * * The LoggedFilter class is a Servlet filter that is used to restrict access
 * to certain pages of a web application. Only logged in users ({@link Doctor}
 * and {@link Patient}) are allowed to access the pages specified in the
 * urlPatterns parameter. If a user is not logged in, they will receive the
 * error message <b>'401 - Unauthorised'</b>
 * 
 * @author Diego Torlone
 *
 */
@WebFilter(urlPatterns = { "/Agenda", "/PickAppointment", "/CancelAppointment", "/GestioneAgenda", "/DeleteAppointment",
		"/DeleteTimeband", "/MakeAppointment", "/ModifyAppointment", "/ModifyTimeband", "/MakeTimeband", "/ChatMessage",
		"/Chatroom", "/GetFile", "/Home", "/Notifications" })

public class LoggedFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*
		 * casting the ServletRequest to HttpServletRequest and the ServletResponse to
		 * HttpServletResponse in order to access the session data.
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		/*
		 * retrieves attributes
		 */
		Patient patient = (Patient) httpRequest.getSession().getAttribute("patient");
		Doctor doctor = (Doctor) httpRequest.getSession().getAttribute("medico");
		List<Patient> patients;
		try {
			patients = doctor.getPatients();
		}catch (Exception e) {
			httpResponse.sendError(401, "Unautenticated - Please log in again");
			return;
		}
		RoleStrategy strategy = (RoleStrategy) session.getAttribute("roleStrategy");

		/*
		 * checks if the user is either a doctor or a patient by checking if the session
		 * is not new and that the Doctor and Patient objects, along with the list of
		 * Patients and the RoleStrategy object, are not null.
		 */
		boolean isDoctor = !session.isNew() && doctor != null && patients != null && strategy != null;
		boolean isPatient = !session.isNew() && doctor != null && patient != null && strategy != null;

		/*
		 * If the user is neither a doctor nor a patient, send an error response
		 * with the status code 401 and a message.
		 */
		if (!isDoctor && !isPatient) {
			httpResponse.sendError(401, "Unautenticated - Please log in again");
			return;
		}

		// the request is passed to the next filter
		else {
			chain.doFilter(request, response);
		}
		return;

	}
}
