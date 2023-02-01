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
import javax.servlet.http.HttpSession;

import DAO.AppointmentDAO;
import DAO.TimebandDAO;
import beans.Appointment;
import beans.Doctor;
import beans.Timeband;
import exceptions.DBException;
import utils.ConnectionHandler;

/**
 * Performs a validation task before the request is processed by the servlet.
 *
 * The method checks if the user making the request is authorized to <u>delete
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
@WebFilter("/DeleteAppointment")
public class DeleteAppointmentFilter extends HttpFilter implements Filter {
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	/*
	 * cast the ServletRequest to an HttpServletRequest and ServletResponse to an
	 * HttpServletResponse to access the session data.
	 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");

		// retrieve the appointmentId from the request parameters,
		int appointmentId = -1;
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
		}catch(Exception e) {
			httpResponse.sendError(400, "Parametri errati");
				return;
		}

		//use the AppointmentDAO to get the corresponding Appointment  from the database
		Appointment appointment = null;
		try {
			appointment = new AppointmentDAO(connection).get(appointmentId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}
		
		//check if the appointment exists and that is not in the past
		if (appointment == null) {
			httpResponse.sendError(400, "Appuntamento non valido");
			return;
		}
		if(appointment.getInizio().isBefore(LocalDateTime.now())) {
			httpResponse.sendError(400, "Non puoi eliminare appuntamenti nel passato");
			return;
		}

		
		//Retrieves timeband
		Timeband timeband;
		try {
			timeband = new TimebandDAO(connection).get(appointment.getDisponibilitàId());
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}

		//check if the doctor in the session is the owner of the appointment.
		if(timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(403, "Non puoi eliminare appuntamenti che non ti appartengono");
					return;
		}


		httpRequest.setAttribute("appointment", appointment);
		httpRequest.setAttribute("timeband", timeband);

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}



}
