package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import DAO.NotificationDAO;
import beans.Doctor;
import beans.Notification;
import beans.Patient;
import beans.User;
import exceptions.DBException;
import utils.ConnectionHandler;




/**
 * Servlet implementation class Notifications
 */
@WebServlet("/Notifications")
public class Notifications extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection connection;

	public void init() {
		connection = new ConnectionHandler(getServletContext()).getConnection();
	}

	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// COMPUTE THE USER
		String role = (String) request.getSession().getAttribute("role");
		User user = null;

		if (role.equalsIgnoreCase("patient")) {
			user = (Patient) request.getSession().getAttribute("patient");
		} else if (role.equalsIgnoreCase("doctor")) {
			user = (Doctor) request.getSession().getAttribute("medico");

		} else {
			response.setStatus(404); // 404 NOT FOUND
			return;
		}

		if (user == null) {
			response.setStatus(500);
			return;
		}

		// Ottieni la lista di notifiche relative al'utente
		List<Notification> notifications;

		try {
			notifications = new NotificationDAO(connection).getAll(user);
		} catch (DBException e) {
			response.setStatus(500);
			response.getWriter().print("Errore DB");
			return;
		}

		// Converti in JSON
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm").create();
		String json = gson.toJson(notifications);

		// Manda JSON
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

		// COMPUTE THE USER
		String role = (String) request.getSession().getAttribute("role");
		User user = null;

		if (role.equalsIgnoreCase("patient")) {
			user = (Patient) request.getSession().getAttribute("patient");
		} else if (role.equalsIgnoreCase("doctor")) {
			user = (Doctor) request.getSession().getAttribute("medico");

		} else {
			response.setStatus(404); // 404 NOT FOUND
			return;
		}

		if (user == null) {
			response.setStatus(500);
			return;
		}
		
		//DELETE NOTIFICATIONS
		
		
		try {
			new NotificationDAO(connection).deleteAll(user);
		} catch (DBException e) {
			response.setStatus(500); 
			response.getWriter().print("Errore database");
			return;
		}
		
		return;

	}

}
