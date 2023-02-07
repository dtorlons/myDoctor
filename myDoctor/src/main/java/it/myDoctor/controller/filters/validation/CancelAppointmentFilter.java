package it.myDoctor.controller.filters.validation;

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

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.AppointmentDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Patient;

/**
 * Performs a validation task before the request is processed by the servlet.
 *
 * The method checks if the user making the request is authorized to <u>cancel
 * the appointment </u> and correct parameters were provided in the request.
 *
 * <p>
 * Responds with the following HTTP Status codes:
 * <li><b>400 - Bad request </b> If the parameters provided are incorrect
 * <li><b>403 - Forbidden </b> If the requesting user has no privileges over the
 * operation
 * <li><b>500 - Internal Server Error </b> Should the database interaction fail
 * </p>
 * <br>
 *
 * @author Diego Torlone
 *
 */
@WebFilter("/CancelAppointment")
public class CancelAppointmentFilter extends HttpFilter implements Filter {

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

		Patient patient = (Patient) httpRequest.getSession().getAttribute("patient");

		// retrieve the appointmentId from the request parameters,
		int appointmentId = -1;
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
		} catch (Exception e) {
			httpResponse.sendError(400, "Parametri errati");
			return;
		}


		//use the AppointmentDAO to get the corresponding Appointment  from the database
		AppointmentDAO appointmentDao = new AppointmentDAO(connection);
		Appointment appointment = null;
		try {
			appointment = appointmentDao.get(appointmentId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}

		//check if the appointment exists
		if (appointment == null) {
			httpResponse.sendError(400, "Parametri errati, l'appuntamento non esiste");
			return;
		}

		//check if the patient in the session is the owner of the appointment.
		if (appointment.getPaziente().getId() != patient.getId()) {
			httpResponse.sendError(403, "Non possiedi privilegi per effetturare questa operazione");
			return;
		}
		
		//check if the appointment is in the past		
		if(appointment.getInizio().isBefore(LocalDateTime.now())) {
			httpResponse.sendError(400, "Non puoi cancellare appuntamenti nel passato");
			return;
		}

		/*
		 * If either of the above checks fail, send an error response with the
		 * appropriate HTTP status code and message. Otherwise, set the request attribute
		 * and continue towards the Servlet.
		 */
		httpRequest.setAttribute("appointment", appointment);
		chain.doFilter(request, response);
	}

}
