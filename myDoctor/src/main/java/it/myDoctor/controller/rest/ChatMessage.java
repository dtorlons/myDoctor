package it.myDoctor.controller.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.strategy.RoleStrategy;
import it.myDoctor.model.beans.Message;

/**
 * This Servlet implements the interface user for getting or posting chat messages.
 * 
 * <p>This class uses the Strategy Pattern </p>
 * 
 * @author Diego Torlone
 *
 */
@WebServlet("/ChatMessage")
@MultipartConfig
public class ChatMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * Retrieves the Strategy 
		 * @see RoleStrategy 
		 */
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		
		//Retrieve the messages and handle exceptions
		List<Message> messages;
		try {
			messages = strategy.getChatMessages(request);
		} catch (DBException e) {
			response.setStatus(500);
			response.getWriter().print("Errore database");
			return;			
		} catch (Exception e) {
			response.setStatus(400);
			response.getWriter().print(e.getMessage());
			return;
		}

		//Send messages over as a JSON
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy hh:mm").create();
		String json = gson.toJson(messages);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

		response.setStatus(200);

		return;

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/*
		 * Retrieve user strategy
		 * @see RoleStrategy
		 */
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		//Post message or handle exceptions
		try {
			strategy.postChatMessage(request);
		} catch (IOException | SQLException | DBException e) {
			response.sendError(500);
			response.getWriter().print("Errore interno, riprovare più tardi");
			return;			
		}catch(ServletException e1) {
			response.sendError(400, e1.getMessage());
			return;
		}
		
		response.setStatus(200);
		return;
		
	}

}
