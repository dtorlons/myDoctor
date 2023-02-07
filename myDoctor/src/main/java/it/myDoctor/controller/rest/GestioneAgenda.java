package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.beans.Day;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Patient;

/**
 * This Servlet is the implementation of the Doctor's agenda. It produces the
 * View for the user.
 * 
 * @author diego
 *
 */
@WebServlet("/GestioneAgenda")
public class GestioneAgenda extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	/**
	 * Initialises connection and Template engine
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
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

		// Retrieve doctor and patient list from session
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");		
		List<Patient> pazienti = medico.getPatients();

		//Retreieve parameters given by the user (if given)
		String data = request.getParameter("data");
		LocalDate beginDate;
		try {
			beginDate = LocalDate.parse(data).with(DayOfWeek.MONDAY);
		} catch (Exception e) {
			beginDate = LocalDate.now().with(DayOfWeek.MONDAY);
		}

		/*
		 *  Initialize a list of Days of the week.
		 *  There will be 7 days, from Monday to Sunday. 
		 *  Each of these will be show with their timebands and appointments.
		 */
		List<Day> giorni = new ArrayList<>();
		
		for (int i = 0; i < 7; i++) {
			try {
				giorni.add(new Day(beginDate.plusDays(i), medico, connection));
			} catch (DBException e) {
				response.sendError(500, "Errore database");
				return;
			}
		}

		//Create a list for the template. This is a cosmetic feature.
		List<Integer> minutes = new ArrayList<>();
		for (int i = 5; i <= 60; i = i + 5) {
			minutes.add(i);
		}



		//Pass variables to the template engine and process
		String path = "/WEB-INF/templates/GestioneAgenda.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("dottore", medico);
		ctx.setVariable("dataPrecedente", beginDate.with(DayOfWeek.MONDAY).minusDays(1).toString());
		ctx.setVariable("giorni", giorni);
		ctx.setVariable("dataSuccessiva", beginDate.with(DayOfWeek.SUNDAY).plusDays(1).toString());
		ctx.setVariable("minutes", minutes);
		ctx.setVariable("pazienti", pazienti);
		templateEngine.process(path, ctx, response.getWriter());
		return;

	}

}
