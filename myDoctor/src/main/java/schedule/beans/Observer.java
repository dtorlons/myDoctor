package schedule.beans;

import java.sql.Connection;

import exceptions.DBException;
import exceptions.InsertionException;

/**
 * 	In order to support a system of notifications this is implements an <b>Observer Pattern</b>.
 * 	In particular, this interface contains the methods that must be implemented by the observers (or subscribers)
 * 	in order to achieve the task.
 * 	Given that the notifications are stored in a database, a connection is required.
 * 
 * @see https://en.wikipedia.org/wiki/Observer_pattern
 */
public interface Observer {
	
	/**
	 * All notifications are saved onto the database, so a connection is required and must be set before invoking <i>update()</i>
	 * @param connection A JDBC Connection to a database
	 */
	void setConnection(Connection connection);
	
	/**
	 * 	Posts a new notification for the user in question
	 * 
	 * 	@param notification The text to be posted as a notification
	 * @throws DBException If the interaction with the database fails
	 * @throws InsertionException If the insertion fails
	 */
	void update(String notification) throws DBException, InsertionException;
	
	 
}
