package beans;

import java.sql.Timestamp;

public class Message {

	private int messageId;
	private User sender;
	private User receiver;
	private Timestamp timestap;
	
	public Message(int messageId, User sender, User receiver, Timestamp timestap) {
		super();
		this.messageId = messageId;
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;		
	}

	public Message(User sender, User receiver, Timestamp timestap) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;
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


	
	
	
}
