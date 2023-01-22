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
import exceptions.UpdateException;

import schedule.entities.Timeband;
import utils.ConnectionHandler;


@WebServlet("/ModifyTimeband")
public class ModifyTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	 public void init(){
			connection = new ConnectionHandler(getServletContext()).getConnection();		
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
		
		//guardia
		
		//ottenimento parametri
		
		int timebandId = -1;
		LocalDate date = null;
		LocalTime begin = null;
		LocalTime end = null;
		int length = 0;
		
		try {
			timebandId = Integer.parseInt(request.getParameter("timebandId"));
			date = LocalDate.parse(request.getParameter("data"));
			begin = LocalTime.parse(request.getParameter("inizio"));
			end = LocalTime.parse(request.getParameter("fine"));
			length = Integer.parseInt(request.getParameter("minutes"));
		}catch(Exception e) {
			e.printStackTrace();
			response.getWriter().print("Errore nei parametri");	
			return;
		}
		
		//controllo parametri
		
		if(timebandId <1 || length <5 || length >60 || begin == null || end == null || date == null) {
			response.getWriter().println("Parametri non validi");
			return;
		}
		
		if(LocalDateTime.of(date, begin).isBefore(LocalDateTime.now())){
			response.getWriter().println("Non si possono apportare modifiche ad eventi del passato");
			return;
		}
		
		
		final TimebandDAO timebandDao = new TimebandDAO(connection);
				
		
		Timeband timeband;
		
		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		if(timeband.getMedico().getId() != medico.getId()) {
			response.sendError(400, "Non ha i privilegi");
			return;
		}		
		
		timeband.setInizio(LocalDateTime.of(date, begin));
		timeband.setFine(LocalDateTime.of(date, end));
		
		try {
			timebandDao.update(timeband);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (UpdateException e) {
			response.sendError(400, e.getMessage());
			return;			
		}
		
		String data = timeband.getInizio().toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path);
		
		return;				
	
	}
	
	

}
