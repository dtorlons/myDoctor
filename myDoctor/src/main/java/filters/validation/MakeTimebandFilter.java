package filters.validation;

import java.io.IOException;
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
import javax.servlet.http.HttpSession;

import beans.Doctor;
import schedule.Timeband;

/**
 * Servlet Filter implementation class MakeTimebandFilter
 */
@WebFilter("/MakeTimeband")
public class MakeTimebandFilter extends HttpFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;		
		HttpSession session = httpRequest.getSession();

		Doctor medico = (Doctor) httpRequest.getSession().getAttribute("medico");

		String _data = request.getParameter("data");
		String _inizio = request.getParameter("inizio");
		String _fine = request.getParameter("fine");
		int minuti;

		try {
			minuti = Integer.parseInt(request.getParameter("minutes"));
		} catch (Exception e) {
			response.getWriter().println("Errore nei minuti");
			return;
		}
		
		if (_data == null || _inizio == null || _fine == null) {
			httpResponse.sendError(400, "Parametri vuoti");
			return;
		}

		LocalDate date = LocalDate.parse(_data);
		LocalDateTime inizio = LocalDateTime.of(date, LocalTime.parse(_inizio));
		LocalDateTime fine = LocalDateTime.of(date, LocalTime.parse(_fine));

		if (inizio.isBefore(LocalDateTime.now()) || fine.isBefore(inizio)) {
			response.getWriter().println("Errore nelle date");
			return;
		}

		if (inizio.plusMinutes(5).isAfter(fine)) {
			response.getWriter().println("Durata minima 5 minuti");
			return;
		}

		if (!(minuti >= 5 && minuti <= 60)) {
			response.getWriter().println("Durata fuori dal range");
			return;
		}
		
		Timeband temporaryTimeband = new Timeband(inizio, fine, medico, minuti);
		
		httpRequest.setAttribute("temporaryTimeband", temporaryTimeband);
		
		
		

		chain.doFilter(request, response);
	}

}
