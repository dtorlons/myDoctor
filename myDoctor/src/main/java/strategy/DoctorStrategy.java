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
import beans.Doctor;
import beans.Message;
import beans.Notification;
import beans.Patient;
import exceptions.DBException;
import schedule.Day;
import utils.Toolkit;

public class DoctorStrategy implements RoleStrategy {

	private final Connection connection;

	public DoctorStrategy(Connection connection) {
		this.connection = connection;
	}

	@Override
	public WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws DBException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		Day today = new Day(LocalDate.now(), doctor, connection);

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

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		String path = "/WEB-INF/Chat.html";

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

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Notification> notifications = new NotificationDAO(connection).getAll(doctor);
		return notifications;
	}

	@Override
	public void deleteAllNotifications(HttpServletRequest request) throws DBException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		new NotificationDAO(connection).deleteAll(doctor);
	}

	public List<Message> getChatMessages(HttpServletRequest request) throws DBException {

		// la guardia deve essere stata implementata per la servlet chiamante

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		// puo' generare errore
		int patientId = Integer.parseInt(request.getParameter("patientId"));

		Patient patient = null;

		patient = Toolkit.findPatientById(patients, patientId);

		if (patient == null)
			return null;

		List<Message> messages = new MessageDAO(connection).getAll(patient);

		// annullo gli allegati e gli utenti
		messages.forEach(m -> {
			m.setSender(doctor.getUsername().equals(m.getSender().getUsername()));
			m.setReceiver(null);
			m.setSender(null);
		});

		return messages;

	}

	public void postChatMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException, DBException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		List<Patient> patients = (List<Patient>) request.getSession().getAttribute("pazienti");

		// potrebbe generare errore perchè non è controllato
		int patientId = Integer.parseInt(request.getParameter("patientId"));
		String text = request.getParameter("text");
		Part file = request.getPart("file");

		Patient patient = Toolkit.findPatientById(patients, patientId);

		Message message = new Message(doctor, patient, Timestamp.valueOf(LocalDateTime.now()), text,
				Toolkit.partToBlob(file), (file == null) ? null : file.getSubmittedFileName());

		new MessageDAO(connection).insert(message, patient);

	}

	@Override
	public Archive getArchive(HttpServletRequest request) throws DBException, IOException {

		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");

		// può generare errore, metterlo nel filtro
		int messageId = Integer.parseInt((String) request.getParameter("id"));

		Archive archive = new MessageDAO(connection).getArchive(messageId, doctor);

		return archive;
	}

}
