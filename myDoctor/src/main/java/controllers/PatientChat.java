package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import DAO.DoctorDAO;
import DAO.MessageDAO;
import DAO.PatientDetailsDAO;
import beans.Doctor;
import beans.Message;
import beans.Patient;
import exceptions.DBException;
import utils.BlobUtils;
import utils.ConnectionHandler;

/**
 * Servlet implementation class ChatPatient
 */
@WebServlet("/PatientChat")
@MultipartConfig
public class PatientChat extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;
	
	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
				
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient)request.getSession().getAttribute("patient");
		
		List<Message> messages;
		try {
			messages = new MessageDAO(connection).getAll(doctor);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}
		
		
		
		
		
		//annullo gli allegati e gli utenti
		messages.forEach(m ->{	m.setAttachment(null); 
								m.setSender(patient.getUsername().equals(m.getSender().getUsername()) );
								m.setReceiver(null); 
								m.setSender(null); 
								});
		
		//Mandare json
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm").create();
		String json = gson.toJson(messages);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

		response.setStatus(200);
				
		return;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("CHIAMATA DAL PAZIENTE");
		
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient)request.getSession().getAttribute("patient");
		
		
		
		String text = request.getParameter("text");
		Part file  = request.getPart("file");
		
		
		
		Message message = null;	
			
		
		try {
			message = new Message(	patient, 
									doctor, 
									java.sql.Timestamp.valueOf(LocalDateTime.now()), 
									text, 
									BlobUtils.partToBlob(file), 
									(file == null)? null : file.getSubmittedFileName());
			
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
			response.sendError(500, "Errore database");
			return;			
		}
		
		response.setStatus(200);
		return;
		
		
	}

}
