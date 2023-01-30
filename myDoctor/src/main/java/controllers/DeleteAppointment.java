package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import schedule.Appointment;
import schedule.Timeband;
import utils.ConnectionHandler;


@WebServlet("/DeleteAppointment")
public class DeleteAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    
    public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		Appointment appointment = (Appointment)request.getAttribute("appointment");
		
		try {
			new AppointmentDAO(connection).delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}

		
		
		
		//AVVISA TUTTI
		Timeband timeband = (Timeband) request.getAttribute("timeband");
		
		appointment.getPaziente().setConnection(connection);
		timeband.getMedico().setConnection(connection);
		
		try {
			appointment.getPaziente().update("Il tuo dottore ha cancellato l'appuntamento del " + appointment.getInizio().toLocalDate() + " alle " + appointment.getInizio().toLocalTime() + "." );
			timeband.getMedico().update("Hai cancellato l'appuntamento del " + appointment.getInizio().toLocalDate() + " alle " + appointment.getInizio().toLocalTime());
		} catch (DBException e) {
			System.err.println("Unable to post notifications\n"+e.getMessage()+"\n" +e.getLocalizedMessage());
		} 
		
		String data = timeband.getInizio().toLocalDate().toString(); 
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data="+data;;
		response.sendRedirect(path);		
		
		return;
	}

}
