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
import utils.ConnectionHandler;

/**
 * Servlet implementation class CheckDoctorCredentials
 */
@WebServlet("/CheckDoctorCredentials")
public class CheckDoctorCredentials extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.getSession().invalidate();
		
		//il filtro qui controlla i parametri username e password non nulli;
		
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();

		
		Doctor doctor = null;
		
		try {
			doctor = new DoctorDAO(connection).checkCredentials(username, password);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;			
		}
		
		if(doctor == null) {
			response.sendError(403, "Utente non esistente");
			return;
		}
		
		
		
		List<Patient> patients;
		
		try {
			patients = new PatientDAO(connection).getAll(doctor);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;	
		}
		
		request.getSession().setAttribute("medico", doctor);
		request.getSession().setAttribute("pazienti", patients);  
		request.getSession().setAttribute("role", "doctor"); // Questa cosa mi serve solo per una servlet....pensaci (request.setattribute?)
		
		//REDIRECT TO HOME PAGE
		
		return;
		
		
	}

}
