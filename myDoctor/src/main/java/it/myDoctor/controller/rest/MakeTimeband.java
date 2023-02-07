package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.exceptions.InsertionException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.TimebandDAO;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Timeband;

/**
 * This Servlet is called when a Doctor inserts a Timeband in a given day
 * 
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/MakeTimeband")
public class MakeTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;


	/**
	 * Initialise connection to the database
	 */
	@Override
	public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Retrieves doctor from the session
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");

		//Retrieves the temporary Timeband object
		Timeband temporaryTimeband = (Timeband )request.getAttribute("temporaryTimeband");

		//Insert the timeband on the database
		final TimebandDAO timebandDao = new TimebandDAO(connection);
		try {
			timebandDao.insert(temporaryTimeband, medico);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (InsertionException e) {
			response.sendError(400, e.getMessage());
			return;
		}

		//Redirect to Doctor's Agenda Management
		String data = temporaryTimeband.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;
		response.sendRedirect(path);
		return;

	}

}
