package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.exceptions.UpdateException;
import it.myDoctor.controller.utils.ConnectionHandler;
import it.myDoctor.model.DAO.TimebandDAO;
import it.myDoctor.model.beans.Timeband;

/**
 *  This Servlet is called upon an Timebans Update request from a Doctor
 *  
 * @author Diego Torlone
 *
 */
@WebServlet("/ModifyTimeband")
public class ModifyTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/*
	 * Initializes connection to the database
	 */
	 @Override
	public void init(){
			connection = new ConnectionHandler(getServletContext()).getConnection();
		}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		//Retrieves timeband from request
		Timeband timeband = (Timeband) request.getAttribute("timeband");

		//Updates the timeband
		try {
			new TimebandDAO(connection).update(timeband);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (UpdateException e) {
			response.sendError(400, e.getMessage());
			return;
		}


		//Redirects to Doctor's Agenda management
		String data = timeband.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;
		response.sendRedirect(path);

		return;

	}



}
