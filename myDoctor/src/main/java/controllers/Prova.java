package controllers;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.MessageDAO;
import beans.Doctor;
import beans.Message;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;

/**
 * Servlet implementation class Prova
 */
@WebServlet("/Prova")
public class Prova extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Prova() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Doctor doctor = null;
		Patient patient = null;
		Timestamp t = null;
		
		
		Message msx = new Message(patient, doctor, t, null, null);
		
		
		//insert patient message
		
		try {
			new MessageDAO(null).insert(msx, msx.getReceiver());
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//retreive patient messages
		
		try {
			new MessageDAO(null).getAll(doctor);
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//--------------
		
		//insert doctor message
		
		try {
			new MessageDAO(null).insert(msx, msx.getReceiver());
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//retreive doctor messages relative to a patient
		
		try {
			new MessageDAO(null).getAll(patient);
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
