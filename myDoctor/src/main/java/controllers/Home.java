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
 * This Servlet is called when the Home Page is requested. It produces the View for the user
 *
 * * <p>This Servlet uses the Strategy design pattern </p>
 * @author Diego Torlone
 *
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	/**
	 * Initializes Template engine
	 */
	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//Retrieves strategy from session
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");

		//Process the template following the strategy given
		try {
			templateEngine.process(	strategy.getHomeTemplate(),
									strategy.getHomeContext(request, response, getServletContext()),
									response.getWriter());

		//Handle exceptions
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}

				
		return;
	}

}
