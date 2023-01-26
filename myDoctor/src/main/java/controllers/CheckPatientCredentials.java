package controllers;

import java.io.IOException;
import java.sql.Connection;

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
 * Servlet implementation class CheckPatientCredentials
 */
@WebServlet("/CheckPatientCredentials")
public class CheckPatientCredentials extends HttpServlet {
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

		//controllo parametri
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Patient patient = null;
		
		try {
			patient = new PatientDAO(connection).chechCredentials(username, password);
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(patient == null) {
			response.sendError(400, "Utente non esistente");
			return;
		}
		
		
		Doctor doctor = null;
		
		try {
			doctor = new DoctorDAO(connection).get(patient.getIdMedico());
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		
		request.getSession().setAttribute("medico", doctor);
		request.getSession().setAttribute("patient", patient);		
		request.getSession().setAttribute("role", "patient");
		
		//REDIRECT TO HOME PAGE
		
		response.sendRedirect("Home");
		
		return;
	}

}
