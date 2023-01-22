package beans;

public class Notification {
	
	private int id;	
	private String text;
	
	public Notification(int id, String text) {
		this.id = id;
		this.text = text;				
	}
	
	public Notification(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
	

}
