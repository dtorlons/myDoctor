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
 * Servlet Filter implementation class Filtro
 */
@WebFilter(urlPatterns = { 	"/GestioneAgenda", 
							"/DeleteAppointment", 
							"/DeleteTimeband", 
							"/MakeAppointment",	
							"/ModifyAppointment", 
							"/ModifyTimeband", 
							"/MakeTimeband" })

public class DoctorFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {		
		

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession session = httpRequest.getSession();		
		
		Doctor doctor = (Doctor)httpRequest.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>)session.getAttribute("pazienti");		
		RoleStrategy strategy = (RoleStrategy) session.getAttribute("roleStrategy");
		
		
		boolean isDoctor = !session.isNew() && doctor != null && patients != null && strategy != null;
		
		if(!isDoctor) {
			httpResponse.sendError(403, "Il tuo ruolo non permette di eseguire questa operazione");
			return;
		}		
		
		

		// pass the request along the filter chain	
		chain.doFilter(request, response);

	}

}
