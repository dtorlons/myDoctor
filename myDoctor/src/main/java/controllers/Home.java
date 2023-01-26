package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.AppointmentDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import schedule.entities.Appointment;
import utils.ConnectionHandler;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;
	private TemplateEngine templateEngine;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
		ServletContext servletContext = getServletContext();		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();  
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");  
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		
		//Computo lista di appuntamenti
		List<Appointment> appointments;

		try {
			appointments = new AppointmentDAO(connection).getAll(patient);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		
		//passo al motore
		String path = "/WEB-INF/Home_patient";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());		
		 
		ctx.setVariable("appuntamenti", appointments);
		ctx.setVariable("paziente", patient);
		ctx.setVariable("dottore", medico);
		templateEngine.process(path, ctx, response.getWriter());
		return;	
		
		
		
		

	}

}
