package database;

import java.sql.Timestamp;

public class StdMessage {

	private String text;
	private String handle_user;
	private String name_chatroom;
	private String ids_mentioned;
	private Timestamp send_date;

	public StdMessage(){
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHandle_user() {
		return handle_user;
	}

	public void setHandle_user(String handle_user) {
		this.handle_user = handle_user;
	}

	public String getName_chatroom() {
		return name_chatroom;
	}

	public void setName_chatroom(String name_chatroom) {
		this.name_chatroom = name_chatroom;
	}

	public String getIds_mentioned() {
		return ids_mentioned;
	}

	public void setIds_mentioned(String ids_mentioned) {
		this.ids_mentioned = ids_mentioned;
	}

	public Timestamp getSend_date() {
		return send_date;
	}

	public void setSend_date(Timestamp send_date) {
		this.send_date = send_date;
	}

}
