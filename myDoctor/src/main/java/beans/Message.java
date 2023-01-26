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
	private String filename;	
	
	
	public Message(int messageId, User sender, User receiver, Timestamp timestap, String message, Blob attachment,
			String filename) {
		super();
		this.messageId = messageId;
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;
		this.message = message;
		this.attachment = attachment;
		this.filename = filename;		
	}


	public Message(User sender, User receiver, Timestamp timestap, String message, Blob attachment, String filename) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.timestap = timestap;
		this.message = message;
		this.attachment = attachment;
		this.filename = filename;
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


	public String getFilename() {
		return filename;
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


	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	private boolean isSender;
	public void setSender(boolean isSender) {
		this.isSender = isSender;
	}	
	
	public boolean getSnd() {
		return this.isSender;
	}
		
	@Override
	public String toString() {
		return "Message [sender=" + sender + ", receiver=" + receiver + ", timestap=" + timestap + ", message="
				+ message + ", attachment=" + attachment + ", filename=" + filename + "]\n";
	}
		
	
}
