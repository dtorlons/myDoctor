package controllers;

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

import DAO.AppointmentDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import schedule.entities.Appointment;
import schedule.entities.Day;
import utils.ConnectionHandler;

/**
 * Servlet implementation class Agenda
 */
@WebServlet("/Agenda")
public class Agenda extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	private Connection connection;
	private TemplateEngine templateEngine;

	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();		
		ServletContext servletContext = getServletContext();		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();  
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");        
	}
	       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient)request.getSession().getAttribute("patient");
		
		List<Day> daysOfWeek = new ArrayList<>();
		
		//Ottenimento dei parametri (Se dati)
		
		String data = request.getParameter("data");
		LocalDate beginDate;
		try {
			 beginDate = LocalDate.parse(data).with(DayOfWeek.MONDAY);			 
		}catch(Exception e) {			
			beginDate = LocalDate.now().with(DayOfWeek.MONDAY);
		}	
		
		for(int i = 0; i<7; i++) {		
			try {
				daysOfWeek.add(new Day(beginDate.plusDays(i), doctor, connection));
			} catch (DBException e) {
				response.sendError(500, "Errore database");
				return;
			}	  
		}	
		
		List<Appointment> appointments;
		
		try {
			appointments = new AppointmentDAO(connection).getAll(patient);
		} catch (DBException e) {
			response.sendError(500,"Errore database");
			return;
		}
		
		
		String path = "/WEB-INF/Agenda.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());		
		ctx.setVariable("dataPrecedente", beginDate.with(DayOfWeek.MONDAY).minusDays(1).toString());  
		ctx.setVariable("giorni", daysOfWeek);
		ctx.setVariable("isEmpty", appointments.isEmpty());
		ctx.setVariable("appuntamenti", appointments);
		ctx.setVariable("dottore", doctor);
		ctx.setVariable("dataSuccessiva", beginDate.with(DayOfWeek.SUNDAY).plusDays(1).toString());		
		ctx.setVariable("paziente", patient);
		templateEngine.process(path, ctx, response.getWriter());
		return;	
		
	}

	

}
