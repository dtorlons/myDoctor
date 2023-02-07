package it.myDoctor.controller.strategy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.thymeleaf.context.WebContext;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.model.beans.Archive;
import it.myDoctor.model.beans.Message;
import it.myDoctor.model.beans.Notification;

/**
 * 
 * The RoleStrategy interface defines the behavior of various role strategies.
 * <p>
 * This interface is used as a part of the <b><u>Strategy design pattern.</u></b> Each role
 * strategy defines the <i>Home</i> context and template path as well as the
 * <i>Chatroom</i> context and template path, the functionality to get and post
 * <i>Messages</i> and <i>Notifications</i>, and get an <i>Archive</i>
 * (Attachment) for a specific role.
 * </p>
 * 
 * @author Diego Torlone
 * 
 */

public interface RoleStrategy {

	/**
	 * 
	 * Returns the home context for a specific role.
	 * 
	 * @param request        an {@link HttpServletRequest}
	 * @param response       an {@link HttpServletResponse}
	 * @param servletContext an {@link ServletContext}
	 * @return the Home context for a specific role
	 * @throws DBException if there is a database error
	 */
	WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext)
			throws DBException;

	/**
	 * 
	 * Returns the Home template path for a specific role.
	 * 
	 * @return the home template path for a specific role
	 */
	String getHomeTemplate();

	/**
	 * 
	 * Returns the Chatroom context for a specific role.
	 * 
	 * @param request        an {@link HttpServletRequest}
	 * @param response       an {@link HttpServletResponse}
	 * @param servletContext an {@link ServletContext}
	 * @return the Chatroom context for a specific role
	 */
	WebContext getChatroomContext(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext);

	/**
	 * 
	 * Returns the Chatroom template path for a specific role.
	 * 
	 * @return the chatroom template path for a specific role
	 */
	String getChatroomTemplate();

	/**
	 * 
	 * Returns the list of {@link Notification} for a specific role.
	 * 
	 * @param request the {@link HttpServletRequest} object
	 * @return the list of notifications for a given role
	 * @throws DBException if there is a database error
	 */
	List<Notification> getNotifications(HttpServletRequest request) throws DBException;

	/**
	 * 
	 * Deletes all {@link Notification} of a given {@link User} for a specific role.
	 * 
	 * @param request the {@link HttpServletRequest}
	 * @throws DBException if there is a database error
	 */
	void deleteAllNotifications(HttpServletRequest request) throws DBException;

	/**
	 * 
	 * Returns the list of {@link Message} for a specific role.
	 * 
	 * @param request the {@link HttpServletRequest}
	 * @return the list of messages for a specific role
	 * @throws DBException if there is a database error
	 * @throws Exception if a generic error occurs
	 */
	public List<Message> getChatMessages(HttpServletRequest request) throws DBException, Exception;

	/**
	 * 
	 * Posts a {@link Message} for a specific role.
	 * 
	 * @param request the {@link HttpServletRequest} 
	 * @throws IOException      if there is an I/O error
	 * @throws ServletException if there is a Servlet error
	 * @throws SerialException  if there is a serialization error
	 */
	void postChatMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException, DBException;

	/**
	 * Returns an {@link Archive} (filename, Blob) following a request sent to {@link GetFile}.
	 * @param request the {@link HttpServletRequest} 
	 * @return an {@link Archive} ready to be sent over an {@link OutputStream}
	 * @throws DBException if there is a database error
	 * @throws IOException if I/O operation fails
	 * @throws Exception if a generic error occurs
	 */
	public Archive getArchive(HttpServletRequest request) throws DBException, IOException, Exception;

}
