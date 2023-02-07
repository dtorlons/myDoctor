package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.strategy.PatientStrategy;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.DoctorDAO;
import it.myDoctor.model.DAO.PatientDAO;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Patient;


/**
 * This Servlet is called when a Patient makes an attempt to log in providing 'username' and 'passord'
 * 
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/CheckPatientCredentials")
public class CheckPatientCredentials extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;

	/*
	 * Initialises connection to the database
	 */
	@Override
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Kills session, if there is one alive
		request.getSession().invalidate();

		
		//Retrieves input parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//Fetches patient from database
		Patient patient = null;
		try {
			patient = new PatientDAO(connection).chechCredentials(username, password);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}

		//Validates patient
		if(patient == null) {
			response.sendError(400, "Utente non esistente");
			return;
		}

		//Retrieves his doctor
		Doctor doctor = null;
		try {
			doctor = new DoctorDAO(connection).get(patient.getIdMedico());
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}


		//Set session attributes
		request.getSession().setAttribute("medico", doctor);
		request.getSession().setAttribute("patient", patient);
		request.getSession().setAttribute("roleStrategy", new PatientStrategy(connection));

		//redirect to home page 
		response.sendRedirect("Home");

		return;
	}

}
