package filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.PatientDAO;
import DAO.TimebandDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import schedule.Appointment;
import schedule.Timeband;
import utils.ConnectionHandler;
import utils.Toolkit;

/**
 * Servlet Filter implementation class MakeAppointmentFilter
 */
@WebFilter("/MakeAppointment")
public class MakeAppointmentFilter extends HttpFilter implements Filter {
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;		
		HttpSession session = httpRequest.getSession();		

		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) httpRequest.getSession().getAttribute("pazienti");

		Integer disponibilitaId;
		LocalDate date;
		LocalDateTime inizio;
		LocalDateTime fine;
		Integer idPaziente;
		String note;

		try {
			disponibilitaId = Integer.parseInt(request.getParameter("disponibilitaId"));
			date = LocalDate.parse(request.getParameter("date"));
			inizio = LocalDateTime.of(date, LocalTime.parse(request.getParameter("inizio")));
			fine = LocalDateTime.of(date, LocalTime.parse(request.getParameter("fine")));
			idPaziente = Integer.parseInt(request.getParameter("idPaziente"));
			note = "" + request.getParameter("note");
		} catch (Exception e) {
			response.getWriter().println("Errore nei parametri in ingresso");
			e.printStackTrace();
			return;
		}

		
		Patient paziente = Toolkit.findPatientById(patients, idPaziente);
		

		Appointment appointment = new Appointment(disponibilitaId, inizio, fine, paziente, note);
		Timeband timeband = null;

		try {
			timeband = new TimebandDAO(connection).get(disponibilitaId);
		} catch (DBException e1) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}

		if (timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(403, "Non hai privilegi per effettuare questa operazione");
			return;
		}

		if (!appointment.getInizio().toLocalDate().equals(appointment.getFine().toLocalDate())) {
			response.getWriter().println("L'appuntamento deve essere nella stessa data");
			return;
		}

		if (appointment.getInizio().isBefore(LocalDateTime.now())) {
			response.getWriter().println("Non si può inserire un appuntamento prima di adesso");
			return;
		}
		
		
		
		httpRequest.setAttribute("appointment", appointment);
		httpRequest.setAttribute("timeband", timeband);		

		chain.doFilter(request, response);
	}

}
