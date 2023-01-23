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

import beans.Doctor;
import beans.Patient;

/**
 * Servlet Filter implementation class PatientFilter
 */
@WebFilter(urlPatterns = { "/Agenda", "/PickAppointment", "/CancelAppointment"})
public class PatientFilter extends HttpFilter implements Filter {
       
   
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession sesh = httpRequest.getSession();			
		
		Patient self = (Patient)httpRequest.getSession().getAttribute("patient");
		Doctor doctor = (Doctor)httpRequest.getSession().getAttribute("medico");

		
		/**
		 * Si tratta di un paziente se ha un attributo medico ed uno paziente
		 */
		
		if(sesh.isNew() || self == null || doctor == null){
			httpResponse.sendError(403, "Non autenticato - Filtro");
			return;
		}
		
		System.out.println("Filtrato -> Sei un paziente");
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	

}
