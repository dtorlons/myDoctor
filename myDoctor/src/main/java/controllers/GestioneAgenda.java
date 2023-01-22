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
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import schedule.entities.Day;
import utils.ConnectionHandler;

@WebServlet("/GestioneAgenda")
public class GestioneAgenda extends HttpServlet {
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
	
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
						
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
				
		List<Patient> pazienti = null;
		try {
			pazienti = (List<Patient>) request.getSession().getAttribute("pazienti");			
		}
		catch(Exception e) {
			response.getWriter().print("Class Cast Exception");
			return;
		}
		
		
		
		List<Day> giorni = new ArrayList<>();	
		
		String data = request.getParameter("data");
		LocalDate beginDate;
		try {
			 beginDate = LocalDate.parse(data).with(DayOfWeek.MONDAY);			 
		}catch(Exception e) {			
			beginDate = LocalDate.now().with(DayOfWeek.MONDAY);
		}		
		
						
		for(int i = 0; i<7; i++) {		
			try {
				giorni.add(new Day(beginDate.plusDays(i), medico, connection));
			} catch (DBException e) {
				response.sendError(500, "Errore database");
				return;
			}	  
		}		
		
		
		List<Integer> minutes = new ArrayList<>();
		for(int i=5; i<=60; i=i+5) {minutes.add(i);}
		
		//Passare al templateEngine          
		
		String path = "/WEB-INF/GestioneAgenda.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());		
		ctx.setVariable("dataPrecedente", beginDate.with(DayOfWeek.MONDAY).minusDays(1).toString());  
		ctx.setVariable("giorni", giorni);
		ctx.setVariable("dataSuccessiva", beginDate.with(DayOfWeek.SUNDAY).plusDays(1).toString());
		ctx.setVariable("minutes", minutes);
		ctx.setVariable("pazienti", pazienti);
		templateEngine.process(path, ctx, response.getWriter());
		return;
		
	}

	

}



