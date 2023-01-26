package controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.DoctorDAO;
import DAO.PatientDetailsDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import utils.ConnectionHandler;

/**
 * Servlet implementation class LoginPaziente
 */
@WebServlet("/LoginPaziente")
public class LoginPaziente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private Connection connection;
	    
	    public void init(){
			connection = new ConnectionHandler(getServletContext()).getConnection();		
		}
    
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getSession().invalidate();
		
		Patient patient = null;
		
		try {
			patient = new Patient(1, "mario", 1, "banana", new PatientDetailsDAO(connection).get(1));
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Doctor doctor = null;
		try {
			 doctor = new DoctorDAO(connection).get(patient.getIdMedico());			 
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		request.getSession().setAttribute("medico", doctor);
		request.getSession().setAttribute("patient", patient); 
		request.getSession().setAttribute("role", "patient");
		
		response.getWriter().println(request.getSession().getAttribute("patient"));
		response.getWriter().println(request.getSession().getAttribute("medico"));
		
		return;
		
		
		
		
	}

	

}
