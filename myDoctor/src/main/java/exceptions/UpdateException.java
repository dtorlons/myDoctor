package exceptions;

/**
 *<p>A custom exception. It is thrown when some business rule is not adhered to.</p> 
 *<i>This can happen, for example, when the user makes an attempt to move an Appointment and it overlaps with another Appointment</i>*
 */
public class UpdateException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	/**
	 * Constructs a new exception from a given message
	 * 
	 * @param Message to display
	 * 
	 */
	public UpdateException(String message) {
		this.message = message;
	}
	
	/**
	 * <p>Returns a text message to be displayed to the user following a business rule violation.</p> 
	 * 
	 * @return A String with a message for the user
	 */
	public String getMessage() {
		return this.message;
	}

}
