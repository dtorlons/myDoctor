package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.PatientDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import utils.ConnectionHandler;

/**
 * Servlet implementation class Login
 */
@WebServlet("/LoginMedico")
public class LoginMedico extends HttpServlet {
	private static final long serialVersionUID = 1L;       
    
	 private Connection connection;
	    
	    public void init(){
			connection = new ConnectionHandler(getServletContext()).getConnection();		
		}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Doctor medico = new Doctor(1, "Diego", "banana");
		
		List<Patient> pazienti;
		try {
			pazienti = new PatientDAO(connection).getAll(medico);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
				
		request.getSession().setAttribute("medico", medico);
		request.getSession().setAttribute("pazienti", pazienti);  
		
		String s = "Logged In as: " +request.getSession().getAttribute("medico").toString() +"\n"+ 
					"Pazienti:\n"; 
		
		List<Patient> paz = (List<Patient>) request.getSession().getAttribute("pazienti");
		
		for(Patient p : paz) {
			s = s+p.toString() +"\n";  
			
		}
				
				
		response.getWriter().print(s);
		return;		
	}

	
	

}
