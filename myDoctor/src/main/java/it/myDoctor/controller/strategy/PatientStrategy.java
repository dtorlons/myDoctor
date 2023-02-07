package it.myDoctor.controller.strategy;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialException;

import org.thymeleaf.context.WebContext;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.utils.Toolkit;
import it.myDoctor.model.DAO.AppointmentDAO;
import it.myDoctor.model.DAO.MessageDAO;
import it.myDoctor.model.DAO.NotificationDAO;
import it.myDoctor.model.beans.Appointment;
import it.myDoctor.model.beans.Archive;
import it.myDoctor.model.beans.Doctor;
import it.myDoctor.model.beans.Message;
import it.myDoctor.model.beans.Notification;
import it.myDoctor.model.beans.Patient;

/**
 * Please see {@link RoleStrategy}
 * @author Diego Torlone
 *
 */
public class PatientStrategy implements RoleStrategy {

	private final Connection connection;

	public PatientStrategy(Connection connection) {
		this.connection = connection;
	}

	@Override
	public WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws DBException {

		// retrieve the patient and doctor from the current session
		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// get all the appointments
		List<Appointment> appointments = new AppointmentDAO(connection).getAll(patient);

		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		// Set the variables for the WebContext
		ctx.setVariable("appuntamenti", appointments);
		ctx.setVariable("paziente", patient);
		ctx.setVariable("dottore", medico);

		return ctx;
	}

	@Override
	public String getHomeTemplate() {
		return "/WEB-INF/templates/Home_patient";
	}

	@Override
	public WebContext getChatroomContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) {
		// Retrieve the patient and doctor from the session
		Patient patient = (Patient) request.getSession().getAttribute("patient");
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		// Set variables in the context
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("paziente", patient);
		ctx.setVariable("dottore", doctor);

		return ctx;

	}

	@Override
	public String getChatroomTemplate() {
		return "/WEB-INF/templates/Chat.html"; 
	}

	@Override
	public List<Notification> getNotifications(HttpServletRequest request) throws DBException {

		// Retrieve the patient from the session
		Patient patient = (Patient) request.getSession().getAttribute("patient");
		// Fetch list of notifications
		List<Notification> notifications = new NotificationDAO(connection).getAll(patient);
		return notifications;
	}

	@Override
	public void deleteAllNotifications(HttpServletRequest request) throws DBException {

		// Retrieve the patient from the session
		Patient patient = (Patient) request.getSession().getAttribute("patient");
		// Delete all the patient's notifications
		new NotificationDAO(connection).deleteAll(patient);

	}

	@Override
	public List<Message> getChatMessages(HttpServletRequest request) throws DBException {

		// Retrieve the patient
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// Get the list of messages
		List<Message> messages = new MessageDAO(connection).getAll(patient);

		// Hide user data
		messages.forEach(m -> {
			m.setSender(patient.getUsername().equals(m.getSender().getUsername()));
			m.setReceiver(null);
			m.setSender(null);
		});

		return messages;
	}

	@Override
	public void postChatMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException, DBException {

		// Retreive a doctor and a patient
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// Retrieve request parameters
		String text = request.getParameter("text");
		Part file = request.getPart("file");

		// Check validity
		if (text == null)
			throw new ServletException("Messaggio vuoto");

		// Create the message
		Message message = new Message(patient, doctor, java.sql.Timestamp.valueOf(LocalDateTime.now()), text,
				Toolkit.partToBlob(file), (file == null) ? null : file.getSubmittedFileName());

		// Insert the message into the database
		new MessageDAO(connection).insert(message, patient);

		return;

	}

	@Override
	public Archive getArchive(HttpServletRequest request) throws Exception {

		// Retrieve the patient
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// Retrieve the parameters
		int messageId = Integer.parseInt(request.getParameter("id"));
		// Check the parameters
		
		if (messageId < 1)
			throw new Exception("Parametri in ingresso non validi");
		
		//Obtains the archive from the database
		Archive archive = new MessageDAO(connection).getArchive(messageId, patient);

		return archive;

	}

}
