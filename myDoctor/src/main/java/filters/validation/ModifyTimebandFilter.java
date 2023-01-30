package filters.validation;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import schedule.Timeband;
import utils.ConnectionHandler;

/**
 * Servlet Filter implementation class ModifyTimebandFilter
 */
@WebFilter("/ModifyTimeband")
public class ModifyTimebandFilter extends HttpFilter implements Filter {
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

		// guardia

		// ottenimento parametri

		int timebandId = -1;
		LocalDate date = null;
		LocalTime begin = null;
		LocalTime end = null;
		int length = 0;

		try {
			timebandId = Integer.parseInt(request.getParameter("timebandId"));
			date = LocalDate.parse(request.getParameter("data"));
			begin = LocalTime.parse(request.getParameter("inizio"));
			end = LocalTime.parse(request.getParameter("fine"));
			length = Integer.parseInt(request.getParameter("minutes"));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("Errore nei parametri");
			return;
		}

		// controllo parametri

		if (timebandId < 1 || length < 5 || length > 60 || begin == null || end == null || date == null) {
			response.getWriter().println("Parametri non validi");
			return;
		}

		if (LocalDateTime.of(date, begin).isBefore(LocalDateTime.now())) {
			response.getWriter().println("Non si possono apportare modifiche ad eventi del passato");
			return;
		}

		final TimebandDAO timebandDao = new TimebandDAO(connection);

		Timeband timeband;

		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore database");
			return;
		}

		if (timeband.getMedico().getId() != medico.getId()) {
			httpResponse.sendError(400, "Non ha i privilegi");
			return;
		}

		timeband.setInizio(LocalDateTime.of(date, begin));
		timeband.setFine(LocalDateTime.of(date, end));
		
		
		httpRequest.setAttribute("timeband", timeband);

		chain.doFilter(request, response);
	}

}
