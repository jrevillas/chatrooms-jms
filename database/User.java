package database;

import java.sql.Timestamp;

public class User {

	private String handle;
	private String password;
	private Timestamp last_conexion;
	private String current_topic;
	private int state;

	public User() {}

	public String getHandle() { return handle; }

	public User setHandle(String handle) { this.handle = handle; return this; }

	public String getPassword() { return password; }

	public User setPassword(String password) { this.password = password; return this; }

	public Timestamp getLast_conexion() { return last_conexion; }

	public User setLast_conexion(Timestamp last_conexion) { this.last_conexion = last_conexion; return this; }

	public String getCurrent_topic() { return current_topic; }

	public User setCurrent_topic(String current_topic) { this.current_topic = current_topic; return this; }

	public int getState() { return state; }

	public User setState(int state) { this.state = state; return this; }
	
}
