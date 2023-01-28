package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;

import schedule.entities.Appointment;
import schedule.entities.Timeband;
import utils.ConnectionHandler;

/**
 * Servlet implementation class DeleteTimeband
 */
@WebServlet("/DeleteTimeband")
public class DeleteTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Manca la guardia

		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
				
		
		//Processo parametri
		int timebandId;		
		try {
			timebandId = Integer.parseInt((String) request.getParameter("timebandId"));
		}catch(Exception e) {
			e.printStackTrace();
			response.getWriter().print("Parametri non validi");
			return;
		}
		
		if(timebandId<1) {
			response.getWriter().print("Parametri non validi - minore di 1");
			return;
		}
		
		//Controllo che la fascia appartenga al dottore e che non sia nel passato
		
		TimebandDAO timebandDao = new TimebandDAO(connection);
		
		Timeband timeband;
		try {
			timeband = timebandDao.get(timebandId);
		} catch (DBException e) {
			response.sendError(500, "Errore nel database");
			return;
		}
		
		if(timeband == null) {
			response.getWriter().print("Fascia oraria non esistente");
			return;
		}
		
		if(timeband.getMedico().getId() != medico.getId()) {			
			response.getWriter().print("La fascia temporale non appartiene al medico richiedente");
			return;
		}
		
		if(timeband.getInizio().isBefore(LocalDateTime.now())) {
			response.getWriter().print("Non si può eliminare una fascia oraria nel passato");
			return;
		}
				
		
		try {
			timebandDao.delete(timeband);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		
		
				
		
		//Avvisa tutti gli interessati
		for(Appointment appointment: timeband.getAppuntamenti()) {
			appointment.getPaziente().setConnection(connection);
			try {
				appointment.getPaziente().update("Il tuo appuntamento con " + medico.getUsername() + " del " + appointment.getInizio().toLocalDate() + " alle " + appointment.getInizio().toLocalTime() + " è stato cancellato. Contatta il tuo medico per ulteriori dettagli");
			} catch (DBException e) {
				System.err.println(e.getMessage() + "\n " + e.getLocalizedMessage());
				break;
			}
		}
			
		timeband.getMedico().setConnection(connection);
		try {
			timeband.getMedico().update("Hai eliminato la fascia temporale del "+ timeband.getInizio().toLocalDate() + " dalle " + timeband.getInizio().toLocalTime() + " alle " + timeband.getFine().toLocalTime() +". Cancellando " + timeband.getAppuntamenti().size() + " appuntamenti");
		} catch (DBException e) {
			System.err.println(e.getMessage() + "\n " + e.getLocalizedMessage());
		} 
		
		String data = timeband.getInizio().toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path);
		
		return;
		
		
	}

}
