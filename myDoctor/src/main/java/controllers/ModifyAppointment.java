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
import schedule.Appointment;
import utils.ConnectionHandler;

/**
 * Servlet implementation class ModifyAppointment
 */
@WebServlet("/ModifyAppointment")
public class ModifyAppointment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Appointment appointment = (Appointment) request.getAttribute("appointment");

		// Inserisco e gestisco eventuali eccezioni
		try {
			new AppointmentDAO(connection).update(appointment);
		} catch (DBException e) {
			response.sendError(500, "Errore nel database");
			return;
		} catch (UpdateException e) {
			response.sendError(400, e.getMessage());
			return;
		}

		// Avviso i partecipanti (paziente)

		appointment.getPaziente().setConnection(connection);
		try {
			appointment.getPaziente()
					.update("Il tuo medico ha cambiato gli orari del tuo appuntamento. Controlla i tuoi appuntamenti.");
		} catch (DBException e) {
			System.err.println("Unable to post notifications\n" + e.getMessage());
		}

		// Ritorno alla pagina
		String data = appointment.getInizio().toLocalDate().toString();
		String path = this.getServletContext().getContextPath() + "/GestioneAgenda?data=" + data;
		;
		response.sendRedirect(path);

		return;

	}

}
