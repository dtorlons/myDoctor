package controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.MessageDAO;
import beans.Doctor;
import beans.Patient;
import beans.User;
import exceptions.DBException;
import utils.Archive;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetFile
 */
@WebServlet("/GetFile")
public class GetFile extends HttpServlet {
	private static final long serialVersionUID = 1L;     
	private static final int BUFFER_SIZE = 4096;   
	private Connection connection;
    
    public void init(){
		connection = new ConnectionHandler(getServletContext()).getConnection();		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//manca la guardia
		
		//leggo parametri
		Doctor doctor = (Doctor) request.getSession().getAttribute("medico");
		Patient patient = (Patient)request.getSession().getAttribute("patient");

		int messageId = Integer.parseInt((String)request.getParameter("id"));
		User requester = (patient == null)? doctor : patient; 
		
		Archive archive = null;

		try {
			archive = new MessageDAO(connection).getArchive(messageId, requester);
		} catch (DBException e) {
			response.sendError(500, "Errore database");
			return;
		} catch (IOException e) {
			response.sendError(500, "Errore I/O");
			return;
		}
		
		if(archive == null) {
			response.sendError(400, "Archivio non esistente");
			return;
		}
		
		//Set content properties and header attributes for the response
		response.setContentType(archive.getMimeType(getServletContext()));
		response.setContentLength(archive.getContentLength());
		response.setHeader(archive.getHeaderKey(), archive.getHeaderValue());
		
		//Write the file to the client
		OutputStream outputStream = response.getOutputStream();
		
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		
		while( (bytesRead = archive.getInputStream().read(buffer)) !=-1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		
		//Close the resources
		archive.closeInputStream();
		outputStream.close();
		
		//Respond to the client
		response.setStatus(200);
		return;
	}

	
}
