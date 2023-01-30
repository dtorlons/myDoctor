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

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import schedule.Timeband;
import utils.ConnectionHandler;

/**
 * Servlet Filter implementation class DeleteTimebandFilter
 */
@WebFilter("/DeleteTimeband")
public class DeleteTimebandFilter extends HttpFilter implements Filter {
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

		// Processo parametri
		int timebandId;
		try {
			timebandId = Integer.parseInt((String) request.getParameter("timebandId"));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("Parametri di tipo errato");
			return;
		}

		if (timebandId < 1) {
			response.getWriter().print("Parametri errati");
			return;
		}

		// Controllo che la fascia appartenga al dottore e che non sia nel passato

		TimebandDAO timebandDao = new TimebandDAO(connection);

		Timeband timeband;
		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			httpResponse.sendError(500, "Errore nel database");
			return;
		}

		if (timeband == null) {
			response.getWriter().print("Fascia oraria non esistente");
			return;
		}

		if (timeband.getMedico().getId() != medico.getId()) {
			response.getWriter().print("La fascia temporale non appartiene al medico richiedente");
			return;
		}

		if (timeband.getInizio().isBefore(LocalDateTime.now())) {
			response.getWriter().print("Non si può eliminare una fascia oraria nel passato");
			return;
		}
		
		httpRequest.setAttribute("timeband", timeband);

		chain.doFilter(request, response);
	}

}
