package it.myDoctor.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.strategy.RoleStrategy;
import it.myDoctor.model.beans.Notification;

/*
 * This Servlet is called when the client request the User's notifications
 * 
 * <p>It uses the Strategy design pattern</p>
 */

@WebServlet("/Notifications")
public class Notifications extends HttpServlet {
	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*Retrieve a Strategy from the session
		 * 
		 * @see RoleStrategy
		 */
		
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");

		/*
		 * Obtain the list of notifications related to the requesting User
		 */
		List<Notification> notifications;
		try {
			notifications = strategy.getNotifications(request);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		}


		// Converts to JSON
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm").create();
		String json = gson.toJson(notifications);

		// Send JSON
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

		response.setStatus(200);
		return;

	}




	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");

		try {
			strategy.deleteAllNotifications(request);
		} catch (DBException e) {
			response.setStatus(500);
			response.getWriter().print("Errore database");
			return;
		}

		response.setStatus(200);
		return;

	}

}
