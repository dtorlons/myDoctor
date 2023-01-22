package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;



import beans.Patient;
import exceptions.DBException;

import exceptions.UpdateException;
import schedule.entities.Appointment;

import utils.ConnectionHandler;

/**
 * Servlet implementation class ModifyAppointment
 */
@WebServlet("/ModifyAppointment")
public class ModifyAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	 public void init(){
			connection = new ConnectionHandler(getServletContext()).getConnection();		
		}  
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		@SuppressWarnings("unchecked")
		List<Patient> pazienti = (List<Patient>) request.getSession().getAttribute("pazienti");
		
		//ottengo e controllo parametri
		
		int appointmentId = -1;
		LocalDate date = null;
		LocalTime begin = null;
		LocalTime end = null;
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
			date = LocalDate.parse(request.getParameter("data"));
			begin = LocalTime.parse(request.getParameter("inizio"));
			end = LocalTime.parse(request.getParameter("fine"));			
		}
		catch(Exception e) {
			response.getWriter().println("Errore nei parametri");
			return;
		}
		
		if(appointmentId<1 || date == null || begin == null || end == null) {
			response.getWriter().println("Parametri non validi");
			return;
		}
		
		if(date.isBefore(LocalDate.now()) || begin.isAfter(end)) {
			response.getWriter().println("Orari non validi");
			return;
		}
		
		boolean isPatient = false;
		
		
		
		final AppointmentDAO appointmentDao = new AppointmentDAO(connection);
		
		//Prendo l'appuntamento originale
		Appointment appointment;
		try {
			appointment = appointmentDao.get(appointmentId);
		} catch (DBException e1) {
			response.sendError(500, "Errore database");
			return;
		}
		
		for(Patient p: pazienti) {
			if(p.getId()==appointment.getPaziente().getId()) {
				isPatient = true;
				break;
			}
		}
		
		if(!isPatient) {
			response.sendError(400, "Non hai privilegi per effettuare questa modifica");
			return;
		}
		
		
		// MODELLO:
		
		//Gli cambio le ore
		appointment.setInizio(LocalDateTime.of(date, begin));
		appointment.setFine(LocalDateTime.of(date, end));
		
		
		
		//Inserisco e gestisco eventuali eccezioni
		try {
			appointmentDao.update(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore nel database");
			return;
		} catch (UpdateException e) {
			response.sendError(400, e.getMessage());
			return;
		}
		
		//Avviso i partecipanti (paziente)
		
		appointment.getPaziente().setConnection(connection);
		try {
			appointment.getPaziente().update("Il tuo medico ha cambiato gli orari del tuo appuntamento. Controlla i tuoi appuntamenti.");
		} catch (DBException e) {
			System.err.println("Unable to post notifications\n"+e.getMessage());		
		}
		
		
		//Ritorno alla pagina
		String data = date.toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path);
		
		return;
		
		
		
	}

}
