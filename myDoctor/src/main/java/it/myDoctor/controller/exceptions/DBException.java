package it.myDoctor.controller.exceptions;
/**
 *<p>A custom exception. It is thrown when the interaction with the database fails</p>
 *	Indicates a condition that the application may want to watch
 */

public class DBException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception from an SQL Exception
	 *
	 * @param Exception - Originating exception
	 *
	 */
	public DBException(Exception e){
		super(e);
	}


}
