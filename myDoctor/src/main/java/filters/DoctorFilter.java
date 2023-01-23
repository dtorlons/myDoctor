package filters;

import java.io.IOException;
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
import javax.servlet.http.HttpSession;

import beans.Doctor;
import beans.Patient;

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
		
		/**
		 * Si tratta di un medico se ha un attributo medico ed una lista di pazienti
		 */

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession sesh = httpRequest.getSession();		
		
		
		Doctor self = (Doctor)sesh.getAttribute("medico");
		List<Patient> patients = (List<Patient>)sesh.getAttribute("pazienti");
		
		System.out.println(self + "\n" + patients);
		
		if(sesh.isNew() || self == null || patients == null) {
			httpResponse.sendError(403, "Non autenticato - Filtro");
			return;
		}
		

		// pass the request along the filter chain
		System.out.println("Filtrato > sei un dottore");
		
		chain.doFilter(request, response);

	}

}
