package it.myDoctor.controller.filters.validation;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Ensures that the 'username' and 'password' parameters are not null. If either
 * parameter is null, a <b>400 - Bad request </b> error is sent to the client.
 *
 * @author Diego Torlone
 *
 */
@WebFilter(urlPatterns = { "/CheckDoctorCredentials", "/CheckPatientCredentials" })
public class CheckCredentialsFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*
		 * cast and ServletResponse to an HttpServletResponse to access the session data.
		 */
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		

		// retrieve 'username' and 'password' from the request parameters
		
		String username;
		String password;
		
		try {
			username = request.getParameter("username").trim();
			password = request.getParameter("password").trim();
		}
		catch(Exception e) {
			httpResponse.sendError(400, "Uno o piu' campi sono vuoti");
			return;
		}

		// perform check
		if (username == null || password == null) {
			httpResponse.sendError(400, "Uno o piu' campi sono vuoti");
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

}
