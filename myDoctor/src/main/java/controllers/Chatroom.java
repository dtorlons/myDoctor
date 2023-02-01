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

import strategy.RoleStrategy;


/*
 * This servlet is called by the user when requesting the Chatroom
 * 
 * <p>This Servlet uses the Strategy design pattern </p>
 */
@WebServlet("/Chatroom")
public class Chatroom extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	/**
	 * Initialisees Template engine
	 */
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
		
		/*
		 * Retrieve Strategy from session
		 * @see RoleStrategy
		 */
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");		
		
		//Process template
		templateEngine.process(	strategy.getChatroomTemplate(), 
								strategy.getChatroomContext(request, response, getServletContext()), 
								response.getWriter());
		return;
	}

}
