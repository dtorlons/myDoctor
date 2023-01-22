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
import schedule.entities.Timeband;
import utils.ConnectionHandler;

/**
 * 
 * @author diego
 *
 */
@WebServlet("/MakeTimeband")
public class MakeTimeband extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	

	
	public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}

	
	

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/**
		 * MANCA LA GUARDIA ED IL CONTROLLO PARAMETRI
		 */
		
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");		
		
		String _data = request.getParameter("data");
		String _inizio = request.getParameter("inizio");
		String _fine = request.getParameter("fine");
		int minuti;
		
		try {
			minuti = Integer.parseInt(request.getParameter("minutes"));
		}catch(Exception e) {
			response.getWriter().println("Errore nei minuti");
			return;
		}
				
		
		LocalDate date = LocalDate.parse(_data);
		LocalDateTime inizio = LocalDateTime.of(date, LocalTime.parse(_inizio));
		LocalDateTime fine = LocalDateTime.of(date, LocalTime.parse(_fine));
		
		
		if(inizio.isBefore(LocalDateTime.now()) || fine.isBefore(inizio)) {
			response.getWriter().println("Errore nelle date");
			return;
		}
		
		if(inizio.plusMinutes(5).isAfter(fine)) {
			response.getWriter().println("Durata minima 5 minuti");
			return;
		}
		
		if(!(minuti>= 5 && minuti<=60)) {
			response.getWriter().println("Durata fuori dal range");
			return;
		}
				
		
		
		Timeband temporaryTimeband = new Timeband(inizio, fine, medico, minuti);
		
		
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
		
		String data = inizio.toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path); 
		return;
		
		
//		if(!timebandDao.isOverlap(temp)) {
//			timebandDao.putTimeband(temp, medico); // qui c'è da correggere perchè timeband ha già l'attributo medico
//			String data = inizio.toLocalDate().toString(); 
//			String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
//			response.sendRedirect(path); 
//			
//		}else {
//			response.getWriter().print("Non inserita");
//		}
		
		
				
		
	}

}
