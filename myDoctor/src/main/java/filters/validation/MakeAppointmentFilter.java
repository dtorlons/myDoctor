package filters.validation;

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

import DAO.TimebandDAO;
import beans.Appointment;
import beans.Doctor;
import beans.Patient;
import beans.Timeband;
import exceptions.DBException;
import utils.ConnectionHandler;
import utils.Toolkit;

/**
 * This filter retrieves the parameters of an appointment, and performs several
 * validations on them before the request is processed by the servlet
 * 
 * The filter also ensures that the doctor making the appointment is the owner
 * of the timeband and that the appointment is in the same date.
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
@WebFilter("/MakeAppointment")
public class MakeAppointmentFilter extends HttpFilter implements Filter {
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
		 * cast the ServletRequest and ServletResponse objects to HttpServletRequest and
		 * HttpServletResponse, and get the HttpSession from the HttpServletRequest
		 * object. 
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		//Retrieve attributes from the HttpSession.
		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) httpRequest.getSession().getAttribute("pazienti");

		
		Integer disponibilitaId;
		LocalDate date;
		LocalDateTime inizio;
		LocalDateTime fine;
		Integer idPaziente;
		String note;

		//Parse the request parameters
		try {
			disponibilitaId = Integer.parseInt(request.getParameter("disponibilitaId"));
			date = LocalDate.parse(request.getParameter("date"));
			inizio = LocalDateTime.of(date, LocalTime.parse(request.getParameter("inizio")));
			fine = LocalDateTime.of(date, LocalTime.parse(request.getParameter("fine")));
			idPaziente = Integer.parseInt(request.getParameter("idPaziente"));
			note = "" + request.getParameter("note");
		} catch (Exception e) {
			httpResponse.sendError(400, "Errore nei parametri in ingresso");
			return;
		}

		Patient paziente = Toolkit.findPatientById(patients, idPaziente);

		//Create a new Appointment object from the parsed parameters and the patient.
		Appointment appointment = new Appointment(disponibilitaId, inizio, fine, paziente, note);
		Timeband timeband = null;

		//Obtain the related timeband
		try {
			timeband = new TimebandDAO(connection).get(disponibilitaId);
		} catch (DBException e1) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}

		//Checks if the doctor in the timeband is the same as the current patient's doctor
		if (timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(403, "Non hai privilegi per effettuare questa operazione");
			return;
		}

		
		//Validates the appointment start and end times
		if (!appointment.getInizio().toLocalDate().equals(appointment.getFine().toLocalDate())) {
			httpResponse.sendError(400, "L'appuntamento deve essere nella stessa data");
			return;
			}
		if (appointment.getInizio().isBefore(LocalDateTime.now())) {
			httpResponse.sendError(400, "Non si può inserire un appuntamento prima di adesso");
			return;
		}

		
		httpRequest.setAttribute("appointment", appointment);
		httpRequest.setAttribute("timeband", timeband);

		
		//Passe the control to the servlet 
		chain.doFilter(request, response);
	}

}
