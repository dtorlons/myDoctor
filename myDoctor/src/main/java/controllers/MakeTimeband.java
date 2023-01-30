package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import exceptions.InsertionException;
import schedule.Timeband;
import utils.ConnectionHandler;


@WebServlet("/MakeTimeband")
public class MakeTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	

	
	public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");	
		
		
		Timeband temporaryTimeband = (Timeband )request.getAttribute("temporaryTimeband");		
		
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
		
		String data = temporaryTimeband.getInizio().toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path); 
		return;
		
	}

}
