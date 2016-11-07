package database;

import java.sql.Timestamp;

public class Chatroom {

	private int id;
	private String name;
	private String handle_creator;
	private Timestamp create_date;

	public Chatroom(){
	}

	public int getId() { return id; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandle_creator() {
		return handle_creator;
	}

	public void setHandle_creator(String handle_creator) {
		this.handle_creator = handle_creator;
	}

	public Timestamp getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Timestamp create_date) {
		this.create_date = create_date;
	}

	public void setId (int id) { this.id = id; }
}
