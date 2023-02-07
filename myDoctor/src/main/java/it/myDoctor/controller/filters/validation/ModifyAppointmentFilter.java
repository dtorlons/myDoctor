package it.myDoctor.controller.filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.controller.utils.Toolkit;
import it.myDoctor.model.DAO.AppointmentDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Patient;

/**
 * Performs a validation task before the request is processed by the servlet.
 *
 * The method checks if the user making the request is authorized to <u>modify
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
@WebFilter("/ModifyAppointment")
public class ModifyAppointmentFilter extends HttpFilter implements Filter {
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
		Doctor doctor = (Doctor) httpRequest.getSession().getAttribute("medico");		
		List<Patient> pazienti = doctor.getPatients();

		// retrieve the appointmentId from the request parameters,
		int appointmentId = -1;
		LocalDate date = null;
		LocalTime begin = null;
		LocalTime end = null;
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
			date = LocalDate.parse(request.getParameter("data"));
			begin = LocalTime.parse(request.getParameter("inizio"));
			end = LocalTime.parse(request.getParameter("fine"));
		} catch (Exception e) {			
			httpResponse.sendError(400, "Errore nei parametri");
			return;
		}

		// Validation of the parameters
		if (appointmentId < 1 || date == null || begin == null || end == null) {
			httpResponse.sendError(400, "Parametri vuoti presenti");
			return;
		}
		if (date.isBefore(LocalDate.now()) || begin.isAfter(end)) {
			httpResponse.sendError(400, "Non si può modificare un appuntamento nel passato");
			return;
		}

		// Retreives original appointment and checks for existence
		Appointment appointment;
		try {
			appointment = new AppointmentDAO(connection).get(appointmentId);
		} catch (DBException e1) {
			httpResponse.sendError(500, "Errore database");
			return;
		}
		if (appointment == null) {
			httpResponse.sendError(400, "L'appuntamento che si desidera modificare non esiste");
			return;
		}

		// Verifies privileges

		Patient patient = Toolkit.findPatientById(pazienti, appointment.getPaziente().getId());

		if (patient == null) {
			httpResponse.sendError(403, "Il paziente non è tra i tuoi assistiti");
			return;
		}

		// Modifies the original appointment
		appointment.setInizio(LocalDateTime.of(date, begin));
		appointment.setFine(LocalDateTime.of(date, end));

		// pass the request to the Servlet
		httpRequest.setAttribute("appointment", appointment);

		chain.doFilter(request, response);
	}

}
