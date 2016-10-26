import java.sql.*;

public class Queries {

	private static Connection conexion = null;

	/* Metodo para conectarnos a la Base de Datos */
	public static void getConexion(){

		String host = "localhost";
		String usr = "root";
		String passwd = "";

		// Obtener conexion
		String driver = "com.mysql.jdbc.Driver";
		String port = "3306";
		String bd = "chatrooms";

		try {

			Class.forName(driver);
			String url = "jdbc:mysql://"+ host + ":" + port + "/" + bd;
			conexion = DriverManager.getConnection(url, usr, passwd);

		}catch (ClassNotFoundException | SQLException e){
			e.printStackTrace();
		}
	}

	/* Listado de todos los usuarios */
	public static User[] usersList(){

		if(conexion == null)
			getConexion();

		User[] result = null;

		String query = "SELECT * ";
		query += "FROM chatrooms.USER ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			ResultSet rs = sentence.executeQuery();

			result = new User[rs.last() ? rs.getRow() : 0];
			int i = 0;

            rs.beforeFirst();

			while (rs.next()){

				User user = new User();

				user.setId(rs.getInt("id"));
				user.setHandle(rs.getString("handle"));
				user.setPassword(rs.getString("password"));
				user.setLast_conexion(rs.getTimestamp("last_conexion"));
				user.setCurrent_topic(rs.getInt("current_topic"));
				user.setState(rs.getInt("status"));

				result[i] = user;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Listado de todas las salas de chat */
	public static Chatroom[] chatroomsList(){

		if(conexion == null)
			getConexion();

		Chatroom[] result = null;

		String query = "SELECT * ";
		query += "FROM chatrooms.CHATROOM ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			ResultSet rs = sentence.executeQuery();

			result = new Chatroom[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Chatroom chatroom = new Chatroom();

				chatroom.setId(rs.getInt("id"));
				chatroom.setName(rs.getString("name"));
				chatroom.setId_creator(rs.getInt("id_creator"));
				chatroom.setCreate_date(rs.getTimestamp("create_date"));

				result[i] = chatroom;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Listado de todos los mensajes */
	public static Message[] messagesList(){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT * ";
		query += "FROM chatrooms.MESSAGE ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Listado de los mensajes de un usuario */
	public static Message[] messagesUser(int id){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
		query += "WHERE u.id = m.id_user ";
		query += "AND u.id = ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	/* Listado de los mensajes de un chatroom */
	public static Message[] messagesChatroom(int id){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m ";
		query += "WHERE c.id = m.id_chatroom ";
		query += "AND c.id = ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	/* Listado de los mensajes de un usuario en un chatroom */
	public static Message[] messagesUserChatroom(int id_user, int id_chatrooms){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
		query += "WHERE c.id = m.id_chatroom ";
		query += "AND u.id = ? ";
		query += " AND c.id = ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id_user);
			sentence.setInt(2, id_chatrooms);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	/* Listado de todos los mensajes con menciones */
	public static Message[] messagesListMentioned(){

		if(conexion == null)
			getConexion();

		Statement sentence = null;
		Message[] result = null;

		String query = "SELECT * ";
		query += "FROM chatrooms.MESSAGE m ";
		query += "WHERE m.ids_mentioned IS NOT NULL ";

		try {
			sentence = conexion.createStatement();
			ResultSet rs = sentence.executeQuery(query);

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Lista de mensajes con menciones de un usuario */
	public static Message[] messagesMentionedByUser(int id){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
		query += "WHERE u.id = m.id_user ";
		query += "AND m.ids_mentioned IS NOT NULL ";
		query += "AND u.id = ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Lista de mensajes con menciones a un usuario */
	public static Message[] messagesMentionedToUsers(int id){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
		query += "WHERE m.ids_mentioned IS NOT NULL ";
		query += "AND u.id = ? ";
		query += "AND m.ids_mentioned LIKE CONCAT('%',u.id,'%') ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Lista de mensajes con menciones a un usuario en un chat */
	public static Message[] messagesMentionedToUsersChat(int id_user, int id_chatroom){

		if(conexion == null)
			getConexion();

		Message[] result = null;

		String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
		query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
		query += "WHERE m.ids_mentioned IS NOT NULL ";
		query += "AND c.id = ? ";
		query += "AND c.id = m.id_chatroom ";
		query += "AND u.id = ? ";
		query += "AND m.ids_mentioned LIKE CONCAT('%',u.id,'%') ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1,id_chatroom);
			sentence.setInt(2,id_user);
			ResultSet rs = sentence.executeQuery();

			result = new Message[rs.last() ? rs.getRow() : 0];
			int i = 0;

			rs.beforeFirst();

			while (rs.next()){

				Message message = new Message();

				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setId_user(rs.getInt("id_user"));
				message.setId_chatroom(rs.getInt("id_chatroom"));
				message.setIds_mentioned(rs.getString("ids_mentioned"));
				message.setSend_date(rs.getTimestamp("send_date"));

				result[i] = message;

				i++;
			}

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/* Obtener el hash de un usuario */
	public static String getHash(String handle){

		if(conexion == null)
			getConexion();

		String result = "";

		String query = "SELECT u.password ";
		query += "FROM chatrooms.USER u ";
		query += "WHERE u.handle = ? ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1,handle);
			ResultSet rs = sentence.executeQuery();

			int i = 0;

			rs.next();

			result = rs.getString("password");

			sentence.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	public static void main (String [] args) {

		System.out.println("Listado de todos los usuarios\n");
		User.toString(usersList());
		System.out.println();

		System.out.println("Listado de todos las salas de chats\n");
		Chatroom.toString(chatroomsList());
		System.out.println();

		System.out.println("Listado de todos los mensajes\n");
		Message.toString(messagesList());
		System.out.println();

		System.out.println("Listado de los mensajes de un usuario\n");
		Message.toString(messagesUser(1));
		System.out.println();

		System.out.println("Listado de los mensajes de un chatroom\n");
		Message.toString(messagesChatroom(1));
		System.out.println();

		System.out.println("Listado de los mensajes de un usuario en un chatroom\n");
		Message.toString(messagesUserChatroom(1, 1));
		System.out.println();

		System.out.println("Listado de todos los mensajes con menciones\n");
		Message.toString(messagesListMentioned());
		System.out.println();

		System.out.println("Lista de mensajes con menciones de un usuario\n");
		Message.toString(messagesMentionedByUser(2));
		System.out.println();

		System.out.println("Lista de mensajes con menciones a un usuario\n");
		Message.toString(messagesMentionedToUsers(4));
		System.out.println();

		System.out.println("Lista de mensajes con menciones a un usuario en un chat\n");
		Message.toString(messagesMentionedToUsersChat(1, 1));
		System.out.println();

		System.out.println("Obtener el hash de un usuario\n");
		System.out.print(getHash("dmelero"));
		System.out.println();

	}
	*/
}
