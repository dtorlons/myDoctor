package filters;

import java.io.IOException;
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

/**
 * Servlet Filter implementation class Filtro
 */
@WebFilter(urlPatterns = { "/GestioneAgenda", "/DeleteAppointment", "/DeleteTimeband", "/MakeAppointment",
		"/ModifyAppointment", "/ModifyTimeband" })
public class DoctorFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession sesh = httpRequest.getSession();

		if (sesh.isNew() || sesh.getAttribute("medico") == null) {
			httpResponse.sendError(403, "Non autenticato - Filtro");

			return;
		}

		// pass the request along the filter chain
		System.out.println("Filtrato");
		
		chain.doFilter(request, response);

	}

}
