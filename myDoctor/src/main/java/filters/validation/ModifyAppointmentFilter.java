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

import DAO.AppointmentDAO;
import beans.Patient;
import exceptions.DBException;
import schedule.Appointment;
import utils.ConnectionHandler;
import utils.Toolkit;

/**
 * Servlet Filter implementation class ModifyAppointmentFilter
 */
@WebFilter("/ModifyAppointment")
public class ModifyAppointmentFilter extends HttpFilter implements Filter {
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		List<Patient> pazienti = (List<Patient>) httpRequest.getSession().getAttribute("pazienti");

		// ottengo e controllo parametri

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
			response.getWriter().println("Errore nei parametri");
			return;
		}

		if (appointmentId < 1 || date == null || begin == null || end == null) {
			response.getWriter().println("Parametri vuoti presenti");
			return;
		}

		if (date.isBefore(LocalDate.now()) || begin.isAfter(end)) {
			response.getWriter().println("Non si può modificare un appuntamento nel passato");
			return;
		}

		// Prendo l'appuntamento originale
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

		// Verifico se il paziente è nella lista

		Patient patient = Toolkit.findPatientById(pazienti, appointment.getPaziente().getId());

		if (patient == null) {
			httpResponse.sendError(400, "Il paziente non è tra i tuoi assistiti");
			return;
		}

		// Gli cambio le ore
		appointment.setInizio(LocalDateTime.of(date, begin));
		appointment.setFine(LocalDateTime.of(date, end));
		
		
		
		httpRequest.setAttribute("appointment", appointment);
		
		

		chain.doFilter(request, response);
	}

}
