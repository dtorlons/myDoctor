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

import DAO.AppointmentDAO;

import DAO.PatientDAO;
import DAO.TimebandDAO;
import beans.Doctor;
import beans.Patient;
import exceptions.DBException;
import exceptions.InsertionException;
import schedule.entities.Appointment;
import schedule.entities.Timeband;
import utils.ConnectionHandler;

/**
 * Servlet implementation class MakeAppointment
 */
@WebServlet("/MakeAppointment")
public class MakeAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init (){
		connection = new ConnectionHandler(getServletContext()).getConnection();	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
		
		
		
		Integer disponibilitaId;
		LocalDate date;
		LocalDateTime inizio;
		LocalDateTime fine;
		Integer idPaziente;
		String note; 

		try {
			disponibilitaId = Integer.parseInt(request.getParameter("disponibilitaId"));
			date = LocalDate.parse(request.getParameter("date"));
			inizio = LocalDateTime.of(date, LocalTime.parse(request.getParameter("inizio")));
			fine = LocalDateTime.of(date, LocalTime.parse(request.getParameter("fine")));
			idPaziente = Integer.parseInt(request.getParameter("idPaziente"));
			note = "" + request.getParameter("note");
		}catch(Exception e) {
			response.getWriter().println("Errore nei parametri in ingresso");
			e.printStackTrace();
			return;
		}			
	
		
		
		Patient paziente;
		try {
			paziente = new PatientDAO(connection).get(idPaziente);
		} catch (DBException e2) {
			response.sendError(500, "Errore database");
			return;
		}
		
		Appointment appointment = new Appointment(disponibilitaId, inizio, fine, paziente, note);
		Timeband timeband = null;
		
		
		try {
			timeband = new TimebandDAO(connection).get(disponibilitaId);
		} catch (DBException e1) {
			response.sendError(500, "Errore nel database");
			return;			
		}	
		
		if(timeband.getMedico().getId() != medico.getId()) {
			response.sendError(403, "Non hai privilegi per effettuare questa operazione");
			return;
		}
		
		
		if(!appointment.getInizio().toLocalDate().equals(appointment.getFine().toLocalDate())) {
			response.getWriter().println("L'appuntamento deve essere nella stessa data");
			return;
		}
		
		if(appointment.getInizio().isBefore(LocalDateTime.now())) {
			response.getWriter().println("Non si può inserire un appuntamento prima di adesso");
			return;
		} 
		
		
		
		
		//Inserimento appuntamento 
		try {
			new AppointmentDAO(connection).insert(appointment, timeband);
		} catch (DBException e) {
			response.sendError(500, e.getMessage());
			return;
		} catch (InsertionException e) {
			response.sendError(400, e.getMessage());
			return;
		}
		
		String data = inizio.toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path); 
		
		return;
		
	}

}
