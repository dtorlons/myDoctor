package strategy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.thymeleaf.context.WebContext;

import beans.Archive;
import beans.Message;
import beans.Notification;
import exceptions.DBException;

public interface RoleStrategy {

	WebContext getHomeContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws DBException;
	String getHomeTemplate();
	
	
	
	WebContext getChatroomContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext);
	String getChatroomTemplate();
	
	
	
	List<Notification> getNotifications(HttpServletRequest request) throws DBException;
	void deleteAllNotifications(HttpServletRequest request) throws DBException;
	
	
	
	public List<Message> getChatMessages (HttpServletRequest request) throws DBException;
	void postChatMessage(HttpServletRequest request) throws IOException, ServletException, SerialException, SQLException, DBException;
	
	
	public Archive getArchive(HttpServletRequest request) throws DBException, IOException;
	
	
	
}
