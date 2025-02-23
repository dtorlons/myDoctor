package it.myDoctor.controller.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * This Servlet is called when the requests a log out
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
		request.getSession().invalidate(); 
		}catch (Exception e){
			//User already logged out
		}
		
		//Redirect to home page
		response.sendRedirect("/myDoctor");
	}

}
