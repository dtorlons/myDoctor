package it.myDoctor.controller.exceptions;

/**
 *<p>A custom exception. It is thrown when some business rule is violated.</p>
 *<i>This can happen, for example, when the user makes an attempt to insert an Appointment and it overlaps with another Appointment</i>*
 */
public class InsertionException extends Exception{

	private static final long serialVersionUID = 1L;

	private String message;

	/**
	 * Constructs a new exception from a given message
	 *
	 * @param Message to display
	 *
	 */
	public InsertionException(String message) {
		this.message = message;
	}

	/**
	 * <p>Returns a text message to be displayed to the user following a business rule violation.</p>
	 *
	 * @return A String with a message for the user
	 */
	@Override
	public String getMessage() {
		return this.message;
	}

}
