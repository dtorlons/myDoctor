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



@WebServlet("/GetFile")
public class GetFile extends HttpServlet {
	private static final long serialVersionUID = 1L;     
	private static final int BUFFER_SIZE = 4096;      
   

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		
		RoleStrategy strategy = (RoleStrategy) request.getSession().getAttribute("roleStrategy");
		
		Archive archive;
		try {
			archive = strategy.getArchive(request);
		} catch (DBException | IOException e) {
			response.sendError(500, "Errore in lettura dal database");
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
