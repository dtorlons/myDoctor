package beans;

import java.sql.Timestamp;

/**
 * This class represents a notification.
 */
public class Notification {

	private int idNotifica;
	private String username;
	private String text;
	private Timestamp timestamp;


	/*
	 * Constructor for existing notification
	 */
	public Notification(int idNotifica, String username, String text, Timestamp timestamp) {
		super();
		this.idNotifica = idNotifica;
		this.username = username;
		this.text = text;
		this.timestamp = timestamp;
	}


	/*
	 * Constructor for new notification
	 */
	public Notification(String text) {
		this.text = text;
	}


	public int getIdNotifica() {
		return idNotifica;
	}

	/*
	 * Getters & Setters
	 */

	public String getUsername() {
		return username;
	}


	public String getText() {
		return text;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}


	public void setIdNotifica(int idNotifica) {
		this.idNotifica = idNotifica;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setText(String text) {
		this.text = text;
	}


	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}








}
