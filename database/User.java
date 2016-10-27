package services;

import java.sql.Timestamp;

public class User {

	private int id;
	private String handle;
	private String password;
	private Timestamp last_conexion;
	private int current_topic;
	private int state;

	public User(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public int getCurrent_topic(){
		return current_topic;
	}

	public void setCurrent_topic(int current_topic){
		this.current_topic = current_topic;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public static void toString(User[] users){

		System.out.format("%2s%10s%25s%30s%14s%7s\n", "id", "handle", "password", "date", "current_topic", "status");

		for(int i = 0; i < users.length; i++)
			System.out.format("%2d%10s%25s%30tc%14d%7s\n", users[i].getId(), users[i].getHandle(),
					users[i].getPassword(), users[i].getLast_conexion(), users[i].getCurrent_topic(),users[i].getState());
	}

}
