package strategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialException;

import org.thymeleaf.context.WebContext;

import DAO.MessageDAO;
import DAO.NotificationDAO;
import beans.Archive;
import beans.Day;
import beans.Doctor;
import beans.Message;
import beans.Notification;
import beans.Patient;
import exceptions.DBException;
import utils.Toolkit;

/**
 * Please see {@link RoleStrategy}
 * @author Diego Torlone
 *
 */
public class DoctorStrategy implements RoleStrategy {

	private final Connection connection;

	public DoctorStrategy(Connection connection) {
		this.connection = connection;
	}

	@Override
	public WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws DBException {
		
		// retrieve the patient and doctor from the current session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		//Fetches the Schedule Day of the doctor
		Day today = new Day(LocalDate.now(), doctor, connection);

		// Set the variables for the WebContext
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		ctx.setVariable("oggi", today);
		ctx.setVariable("pazienti", patients);
		ctx.setVariable("dottore", doctor);

		return ctx;
	}

	@Override
	public String getHomeTemplate() {
		return "/WEB-INF/Home_doctor";
	}

	@Override
	public WebContext getChatroomContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) {
		// Retrieve the patient and doctor from the session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");
		
		// Set variables in the context
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("pazienti", patients);
		ctx.setVariable("dottore", doctor);

		return ctx;
	}

	@Override
	public String getChatroomTemplate() {
		return "/WEB-INF/Chat.html";
	}

	@Override
	public List<Notification> getNotifications(HttpServletRequest request) throws DBException {

		// Retrieve the Doctor from the session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		// Fetch list of notifications
		List<Notification> notifications = new NotificationDAO(connection).getAll(doctor);
		return notifications;
	}

	@Override
	public void deleteAllNotifications(HttpServletRequest request) throws DBException {

		// Retrieve the doctor from the session
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		// Delete all the doctor's notifications
		new NotificationDAO(connection).deleteAll(doctor);
	}

	@Override
	public List<Message> getChatMessages(HttpServletRequest request) throws Exception {

		
		//Retreives session attributes
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		//Retrieves request parameters
		int patientId = Integer.parseInt(request.getParameter("patientId"));

		if(patientId < 1) throw new Exception("Parametri non validi");
		
		//Retrieve interlocutor
		Patient patient = null;
		patient = Toolkit.findPatientById(patients, patientId);

		//Validate interlocutor
		if (patient == null)
			return null;

		//Get all the messages exchanged with the interlocutor
		List<Message> messages = new MessageDAO(connection).getAll(patient);

		//Hide user data
		messages.forEach(m -> {
			m.setSender(doctor.getUsername().equals(m.getSender().getUsername()));
			m.setReceiver(null);
			m.setSender(null);
		});

		return messages;

	}

	@Override
	public void postChatMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException, DBException {

		//Retrive session attributes (doctor, list of patients)
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		//Retrieve request parameters
		int patientId = Integer.parseInt(request.getParameter("patientId"));
		String text = request.getParameter("text");
		Part file = request.getPart("file");

		//Validate parameters
		if(patientId < 1 || text == null) throw new ServletException("Parametri non validi");
		
		//Retrive patient from doctor's list
		Patient patient = Toolkit.findPatientById(patients, patientId);

		//Create the message object
		Message message = new Message(doctor, patient, Timestamp.valueOf(LocalDateTime.now()), text,
				Toolkit.partToBlob(file), (file == null) ? null : file.getSubmittedFileName());

		//Post the message into the database
		new MessageDAO(connection).insert(message, patient);

	}

	@Override
	public Archive getArchive(HttpServletRequest request) throws Exception {

		// Retrieve the patient
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		// Retrieve the parameters
		int messageId = Integer.parseInt(request.getParameter("id"));
		
		// Check the parameters
		if (messageId < 1)
			throw new Exception("Parametri in ingresso non validi");

		//Obtains the archive from the database
		Archive archive = new MessageDAO(connection).getArchive(messageId, doctor);

		return archive;
	}

}
