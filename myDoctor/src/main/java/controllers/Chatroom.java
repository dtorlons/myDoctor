package controllers;

import java.io.IOException;
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

/**
 * Servlet implementation class Chatroom
 */
@WebServlet("/Chatroom")
public class Chatroom extends HttpServlet {
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

		String role = (String) request.getSession().getAttribute("role");

		/*
		 * qui si potrebbe implementare uno strategy. Te lo spiego: c'è un interfaccia
		 * con il metodo prepareContext(T cot). Nota bene il tipo parametrico. Infatti
		 * l'interfaccia, parametrica rispetto a T, è implementata dalle classi:
		 * -DoctorPreparer (nomi di fantasia) -PatientPrepare
		 * 
		 * Ognuna di queste parametrica rispetto a medico o paziente. Poi vedremo che
		 * parametri passare a questi metodi.
		 * 
		 * Dunque, le ultime due classi sono le concrete strategy; l'interfaccia è
		 * l'interfaccia Stategy. code: Strategy strategy = new Strategy ()
		 */

		if (role.equals("patient")) {

			Patient patient = (Patient) request.getSession().getAttribute("patient");

			String path = "/WEB-INF/Chat.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("paziente", patient);
			templateEngine.process(path, ctx, response.getWriter());
			return;

		} // END ROLE PATIENT
		else if (role.equals("doctor")) {

			Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
			List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

			String path = "/WEB-INF/Chat.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("pazienti", patients);
			ctx.setVariable("dottore", doctor);
			templateEngine.process(path, ctx, response.getWriter());
			return;



		} else {
			response.sendError(403, "No role, not allowed");
			return;
		}

	}

}
