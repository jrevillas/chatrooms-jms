import java.sql.Timestamp;

public class Message {

	private int id;
	private String text;
	private int id_user;
	private int id_chatroom;
	private String ids_mentioned;
	private Timestamp send_date;

	public Message(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}

	public int getId_chatroom() {
		return id_chatroom;
	}

	public void setId_chatroom(int id_chatroom) {
		this.id_chatroom = id_chatroom;
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

	public static void toString(Message[] messages){

		System.out.format("%2s%100s%8s%12s%14s%30s\n", "id", "text", "id_user", "id_chatroom", "ids_mentioned", "send_date");

		for(int i = 0; i < messages.length; i++)
			System.out.format("%2d%100s%8d%12d%14s%30tc\n", messages[i].getId(), messages[i].getText(), messages[i].getId_user(),
					messages[i].getId_chatroom(), messages[i].getIds_mentioned(), messages[i].getSend_date());
	}

}
