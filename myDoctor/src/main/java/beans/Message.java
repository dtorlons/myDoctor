package beans;

import java.sql.Blob;
import java.sql.Timestamp;

public class Message {

	private int messageId;
	private User sender;
	private User receiver;
	private Timestamp timestap;
	private String message;
	private Blob attachment;
	
	
	public Message(int messageId, User sender, User receiver, Timestamp timestap, String message, Blob attachment) {
		super();
		this.messageId = messageId;
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;
		this.message = message;
		this.attachment = attachment;
	}


	public Message(User sender, User receiver, Timestamp timestap, String message, Blob attachment) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;
		this.message = message;
		this.attachment = attachment;
	}


	public int getMessageId() {
		return messageId;
	}


	public User getSender() {
		return sender;
	}


	public User getReceiver() {
		return receiver;
	}


	public Timestamp getTimestap() {
		return timestap;
	}


	public String getMessage() {
		return message;
	}


	public Blob getAttachment() {
		return attachment;
	}


	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}


	public void setSender(User sender) {
		this.sender = sender;
	}


	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}


	public void setTimestap(Timestamp timestap) {
		this.timestap = timestap;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public void setAttachment(Blob attachment) {
		this.attachment = attachment;
	}
	
	
	
	
	
	
	

	
	
	
}
