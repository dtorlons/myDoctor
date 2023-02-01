package filters.validation;

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


import DAO.TimebandDAO;
import beans.Appointment;
import beans.Doctor;
import beans.Patient;
import beans.Timeband;
import exceptions.DBException;
import utils.ConnectionHandler;

/**
 * This filter retrieves the parameters of an appointment, and performs several
 * validations on them before the request is processed by the servlet
 * 
 * The filter also ensures that the patient making the appointment is entitled
 * to do so
 * 
 * If the parameters are invalid, the filter sends an error response to the
 * client.
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
@WebFilter("/PickAppointment")
public class PickAppointmentFilter extends HttpFilter implements Filter {
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
		 * cast the ServletRequest and ServletResponse objects to HttpServletRequest and
		 * HttpServletResponse, and get the HttpSession from the HttpServletRequest
		 * object.
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");
		Patient patient = (Patient) httpRequest.getSession().getAttribute("patient");

		// Retrieve attributes from the HttpSession.
		Integer disponibilitaId;
		LocalDate date;
		LocalDateTime inizio;
		LocalDateTime fine;
		String note;

		// Parse the request parameters
		try {
			disponibilitaId = Integer.parseInt(request.getParameter("disponibilitaId"));
			date = LocalDate.parse(request.getParameter("date"));
			inizio = LocalDateTime.of(date, LocalTime.parse(request.getParameter("inizio")));
			fine = LocalDateTime.of(date, LocalTime.parse(request.getParameter("fine")));
			note = "" + request.getParameter("note");
		} catch (Exception e) {
			response.getWriter().println("Errore nei parametri in ingresso");
			e.printStackTrace();
			return;
		}

		// Create a new Appointment object from the parsed parameters.
		Appointment appointment = new Appointment(disponibilitaId, inizio, fine, patient, note);

		// Retreives the timeband the appointment is supposed to be inserted into
		Timeband timeband = null;
		try {
			timeband = new TimebandDAO(connection).get(disponibilitaId);
		} catch (DBException e1) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}

		// Verify ownership
		if (timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(403, "Non hai privilegi per effettuare questa operazione");
			return;
		}

		// Check date match
		if (!appointment.getInizio().toLocalDate().equals(appointment.getFine().toLocalDate())) {
			httpResponse.sendError(403, "L'appuntamento deve essere nella stessa data");
			return;
		}

		// Check that the appointment is not in the past
		if (appointment.getInizio().isBefore(LocalDateTime.now())) {
			httpResponse.sendError(403, "Non si può inserire un appuntamento prima di adesso");
			return;
		}

		// Pass along the request to the Servlet
		httpRequest.setAttribute("appointment", appointment);
		httpRequest.setAttribute("timeband", timeband);

		chain.doFilter(request, response);
	}

}
