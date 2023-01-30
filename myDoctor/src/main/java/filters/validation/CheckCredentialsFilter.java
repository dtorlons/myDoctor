package filters.validation;

import java.io.IOException;
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

/**
 * Servlet Filter implementation class CheckCredentialsFilter
 */
@WebFilter(urlPatterns = {"/CheckDoctorCredentials", "/CheckPatientCredentials"})
public class CheckCredentialsFilter extends HttpFilter implements Filter {     
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession session = httpRequest.getSession();
		
		
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		
		if(username == null || password == null) {
			httpResponse.sendError(400, "Uno o piu' parametri sono vuoti");
			return;
		}
		
		chain.doFilter(request, response);
	}

	

}
