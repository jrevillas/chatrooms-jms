import java.sql.Timestamp;

public class Chatroom {

	private int id;
	private String name;
	private int id_creator;
	private Timestamp create_date;

	public Chatroom(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId_creator() {
		return id_creator;
	}

	public void setId_creator(int id_creator) {
		this.id_creator = id_creator;
	}

	public Timestamp getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Timestamp create_date) {
		this.create_date = create_date;
	}

	public static void toString(Chatroom[] chatrooms){

		System.out.format("%2s%15s%11s%30s\n", "id", "name", "id_creator", "create_date");

		for(int i = 0; i < chatrooms.length; i++)
			System.out.format("%2d%15s%11s%30tc\n", chatrooms[i].getId(), chatrooms[i].getName(),
					chatrooms[i].getId_creator(), chatrooms[i].getCreate_date());
	}

}
