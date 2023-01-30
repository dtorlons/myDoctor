package controllers;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Notification;
import exceptions.DBException;
import strategy.RoleStrategy;



@WebServlet("/Notifications")
public class Notifications extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		List<Notification> notifications;
		try {
			notifications = strategy.getNotifications(request);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
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
	
		
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		try {
			strategy.deleteAllNotifications(request);
		} catch (DBException e) {
			response.setStatus(500); 
			response.getWriter().print("Errore database");;
			return;
		}
		
		response.setStatus(200);
		return;

	}

}
