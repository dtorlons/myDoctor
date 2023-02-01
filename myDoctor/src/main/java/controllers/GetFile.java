package controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Archive;
import exceptions.DBException;
import strategy.RoleStrategy;


/*
 * This Servlet is called when an User download an attachment from the chat. 
 * 
 * <p>This Servlet uses the Strategy design pattern </p>
 */
@WebServlet("/GetFile")
public class GetFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096;



	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*
		 * Retrieves a strategy from the session
		 * @see RoleStrategy
		 */		
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");

		/*
		 * Obtains the archive (filename, data) to be sent over to the user
		 */
		Archive archive;
		try {
			archive = strategy.getArchive(request);
		} catch (DBException | IOException e) {
			response.sendError(500, "Errore in lettura dal database");
			return;
		} catch (Exception e) {
			response.sendError(400, e.getMessage());
			return;
		} 

		//Validate archive
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
