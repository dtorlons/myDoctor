package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.DoctorDAO;
import DAO.PatientDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import strategy.DoctorStrategy;
import utils.ConnectionHandler;


/**
 * This Servlet is called when a Doctor makes an attempt to log in providing 'username' and 'password'
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/CheckDoctorCredentials")
public class CheckDoctorCredentials extends HttpServlet {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Kill session, if there is already one alive.
		request.getSession().invalidate();

		//Retrieve username and password from request
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();

		//Fetches Doctor from the database upon the given credentials
		Doctor doctor = null;
		try {
			doctor = new DoctorDAO(connection).checkCredentials(username, password);
		} catch (DBException e) {
			e.printStackTrace();
			response.sendError(500, "Errore database");
			return;
		}

		//Validates Doctor
		if (doctor == null) {
			response.sendError(403, "Utente non esistente");
			return;
		}
		
		//Obtains the list of his patients
		List<Patient> patients;
		try {
			patients = new PatientDAO(connection).getAll(doctor);
		} catch (DBException e) {
			e.printStackTrace();
			response.sendError(500, "Errore database");
			return;
		}

		//Set session attributes
		request.getSession().setAttribute("medico", doctor);
		request.getSession().setAttribute("pazienti", patients);
		request.getSession().setAttribute("roleStrategy", new DoctorStrategy(connection));

		//Redirect to home page
		response.sendRedirect("Home");

		return;

	}

}
