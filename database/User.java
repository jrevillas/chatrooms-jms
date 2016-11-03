package database;

import java.sql.Timestamp;

public class User {

	private String handle;
	private String password;
	private Timestamp last_conexion;
	private String current_topic;
	private int state;

	public User(){}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getLast_conexion() {
		return last_conexion;
	}

	public void setLast_conexion(Timestamp last_conexion) {
		this.last_conexion = last_conexion;
	}

	public String getCurrent_topic(){
		return current_topic;
	}

	public void setCurrent_topic(String current_topic){
		this.current_topic = current_topic;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
