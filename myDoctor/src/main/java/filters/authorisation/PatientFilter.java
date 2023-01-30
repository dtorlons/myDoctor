package filters.authorisation;

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
import strategy.RoleStrategy;

/**
 * Servlet Filter implementation class PatientFilter
 */
@WebFilter(urlPatterns = { 	"/Agenda", 
							"/PickAppointment", 
							"/CancelAppointment"})

public class PatientFilter extends HttpFilter implements Filter {
       
   
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession session = httpRequest.getSession();
		
		Patient patient = (Patient)httpRequest.getSession().getAttribute("patient");
		Doctor doctor = (Doctor)httpRequest.getSession().getAttribute("medico");			
		RoleStrategy strategy = (RoleStrategy) session.getAttribute("roleStrategy");

		boolean isPatient = !session.isNew() && doctor != null && patient != null && strategy != null;
		
		if(!isPatient) {
			httpResponse.sendError(403, "Il tuo ruolo non permette di eseguire questa operazione");
			return;
		}
		
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	

}
