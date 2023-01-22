package controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AppointmentDAO;

import DAO.TimebandDAO;
import beans.Doctor;
import exceptions.DBException;
import schedule.entities.Appointment;
import schedule.entities.Timeband;
import utils.ConnectionHandler;


@WebServlet("/DeleteAppointment")
public class DeleteAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    
    public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Manca la guardia
		
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");

		//Controllo parametri
		
		int appointmentId = -1;		
		
		try {
			appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
		}catch(Exception e) {
			e.printStackTrace();
			response.getWriter().println("Parametri errati");
		}
		
		if(appointmentId <1) {
			response.getWriter().print("Id Appuntamento non valido");
			return;
		}
		
		AppointmentDAO appointmentDao = new AppointmentDAO(connection);
		
		Appointment appointment;
		try {
			appointment = appointmentDao.get(appointmentId);
		} catch (DBException e1) {
			response.sendError(500, "Errore database");
			return;
		}
		
		Timeband timeband;
		try {
			timeband = new TimebandDAO(connection).get(appointment.getDisponibilitàId());
		} catch (DBException e) {
			response.sendError(500, "Errore nel database");
			return;
		}
		
		if(timeband.getMedico().getId() != medico.getId()) {
			response.getWriter().println("Non puoi eliminare appuntamenti che non ti appartengono");
			return;
		}
		
		
		try {
			appointmentDao.delete(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
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
