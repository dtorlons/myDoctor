package controllers;

import java.io.IOException;
import java.sql.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.MessageDAO;
import beans.Doctor;
import beans.Message;
import beans.Patient;
import exceptions.DBException;
import utils.Toolkit;
import utils.ConnectionHandler;

@WebServlet("/DoctorChat")
@MultipartConfig
public class DoctorChat extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		// potrebbe generare errore

		int patientId = Integer.parseInt(request.getParameter("patientId"));

		Patient patient = null;

		for (Patient pat : patients) {
			if (pat.getId() == patientId) {
				patient = pat;
				break;
			}
		}

		if (patient == null) {
			response.setStatus(401);
			response.getWriter().println("Il paziente non è nella tua lista pazienti");
			return;
		}

		List<Message> messages = null;

		try {
			messages = new MessageDAO(connection).getAll(patient);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
		}

		
		
		
		
		// annullo gli allegati e gli utenti
		messages.forEach(m -> {
			m.setAttachment(null);
			m.setSender(doctor.getUsername().equals(m.getSender().getUsername()));
			m.setReceiver(null);
			m.setSender(null);
		});

		// Mandare json
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm").create();
		String json = gson.toJson(messages);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

		response.setStatus(200);
		
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		// potrebbe generare errore perchè non è controllato
		int patientId = Integer.parseInt(request.getParameter("patientId"));
		String text = request.getParameter("text");
		Part file = request.getPart("file");

		
		
//		Patient patient = null;
//
//		for (Patient pat : patients) {
//			if (pat.getId() == patientId) {
//				patient = pat;
//				break;
//			}
//		}

		Patient patient = Toolkit.findPatientById(patients, patientId);
		
		
		Message message = null;
		try {
			message = new Message(doctor, patient, Timestamp.valueOf(LocalDateTime.now()), text,
					Toolkit.partToBlob(file), (file == null) ? null : file.getSubmittedFileName());

		} catch (SerialException e1) {
			response.sendError(500, "Unable to serialize file");
			return;
		} catch (IOException e1) {
			response.sendError(500, "I/O Error");
			return;
		} catch (SQLException e1) {
			response.sendError(500, "Errore database");
			return;
		}

		try {
			new MessageDAO(connection).insert(message, patient);
		} catch (DBException e) {
			response.setStatus(500);
			response.getWriter().println(e.getMessage());			
			return;
		}
		
		response.setStatus(200);

		return;

	}

}
