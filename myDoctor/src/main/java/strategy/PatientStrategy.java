package strategy;

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

import DAO.AppointmentDAO;
import DAO.MessageDAO;
import DAO.NotificationDAO;
import beans.Archive;
import beans.Doctor;
import beans.Message;
import beans.Notification;
import beans.Patient;
import exceptions.DBException;
import schedule.Appointment;
import utils.Toolkit;

public class PatientStrategy implements RoleStrategy {

	private final Connection connection;

	public PatientStrategy(Connection connection) {
		this.connection = connection;
	}

	@Override
	public WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws DBException {

		Doctor medico = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// Computo lista di appuntamenti
		List<Appointment> appointments = new AppointmentDAO(connection).getAll(patient);

		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		ctx.setVariable("appuntamenti", appointments);
		ctx.setVariable("paziente", patient);
		ctx.setVariable("dottore", medico);

		return ctx;
	}

	@Override
	public String getHomeTemplate() {
		return "/WEB-INF/Home_patient";
	}

	@Override
	public WebContext getChatroomContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) {

		Patient patient = (Patient) request.getSession().getAttribute("patient");
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("paziente", patient);
		ctx.setVariable("dottore", doctor);

		return ctx;

	}

	@Override
	public String getChatroomTemplate() {
		return "/WEB-INF/Chat.html";
	}

	@Override
	public List<Notification> getNotifications(HttpServletRequest request) throws DBException {

		Patient patient = (Patient) request.getSession().getAttribute("patient");
		List<Notification> notifications = new NotificationDAO(connection).getAll(patient);
		return notifications;
	}

	@Override
	public void deleteAllNotifications(HttpServletRequest request) throws DBException {

		Patient patient = (Patient) request.getSession().getAttribute("patient");
		new NotificationDAO(connection).deleteAll(patient);

	}

	@Override
	public List<Message> getChatMessages(HttpServletRequest request) throws DBException {
		
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		List<Message> messages = new MessageDAO(connection).getAll(patient);

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

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient) request.getSession().getAttribute("patient");

		// può generare errore, parametri non controllati
		String text = request.getParameter("text");
		Part file = request.getPart("file");

		Message message = new Message(patient, doctor, java.sql.Timestamp.valueOf(LocalDateTime.now()), text,
				Toolkit.partToBlob(file), (file == null) ? null : file.getSubmittedFileName());
		
		new MessageDAO(connection).insert(message, patient);
		
		return;

	}

	@Override
	public Archive getArchive(HttpServletRequest request) throws DBException, IOException {


		Patient patient = (Patient)request.getSession().getAttribute("patient");
		
		//può generare errore, metterlo nel filtro
		int messageId = Integer.parseInt((String)request.getParameter("id"));
		
		Archive archive = new MessageDAO(connection).getArchive(messageId, patient);
		
		return archive;
		
		
	}

}
