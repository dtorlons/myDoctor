package controllers;

import java.io.IOException;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import exceptions.DBException;
import strategy.RoleStrategy;


/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	private TemplateEngine templateEngine;

	public void init() {		
		ServletContext servletContext = getServletContext();		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();  
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");  
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		try {
			templateEngine.process(	strategy.getHomeTemplate(), 
									strategy.getHomeContext(request, response, getServletContext()), 
									response.getWriter());
			
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} 
		
		
		return;
	}

}
