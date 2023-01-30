package filters.authentication;

import java.io.IOException;
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

import beans.Doctor;
import beans.Patient;
import strategy.RoleStrategy;

/**
 * Servlet Filter implementation class LoggedFilter
 */
@WebFilter(urlPatterns = { 	"/Agenda", 
							"/PickAppointment", 
							"/CancelAppointment", 
							"/GestioneAgenda", 
							"/DeleteAppointment", 
							"/DeleteTimeband", 
							"/MakeAppointment",
							"/ModifyAppointment", 
							"/ModifyTimeband", 
							"/MakeTimeband", 
							"/ChatMessage", 
							"/Chatroom", 
							"/GetFile", 
							"/Home", 
							"/Notifications"  })

public class LoggedFilter extends HttpFilter implements Filter {
       
    
	public void destroy() {
		// TODO Auto-generated method stub
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//un utente è loggato se ha una tripla (dottore, lista pazienti, strategia) o (dottore, paziente, strategia)
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		
		HttpSession session = httpRequest.getSession();		
		
		Patient patient = (Patient)httpRequest.getSession().getAttribute("patient");
		Doctor doctor = (Doctor)httpRequest.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>)session.getAttribute("pazienti");		
		RoleStrategy strategy = (RoleStrategy) session.getAttribute("roleStrategy");
		
		
		boolean isDoctor = !session.isNew() && doctor != null && patients != null && strategy != null;
		boolean isPatient = !session.isNew() && doctor != null && patient != null && strategy != null;
		
		if(!isDoctor && !isPatient) {
			httpResponse.sendError(401, "Unautenticated - Please log in again");			
			return;
		}
		else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}		

		return;

	}

	

}
