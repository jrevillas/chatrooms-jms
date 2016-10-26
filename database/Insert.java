import java.sql.*;

public class Insert {

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

	/* Añadir a un usuario */
	public static void insertUser(String handle, String password){

		if(conexion == null)
			getConexion();

		String query = "INSERT INTO `chatroom`.`USER` (`handle`, `password`) ";
		query += "VALUES (?,?)";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1, handle);
			sentence.setString(2, password);
			sentence.execute();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Añadir una sala de chat */
	public static void insertChatroom(String name, int id_user){

		if(conexion == null)
			getConexion();

		String query = "INSERT INTO `chatroom`.`CHATROOM` (`name`, `id_creator`) ";
		query += "VALUES (?,?)";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1, name);
			sentence.setInt(2, id_user);
			sentence.execute();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Añadir un usuario a un chat */
	public static void insertInTopic(int id_user, int id_chatroom){

		if(conexion == null)
			getConexion();

		String query = "INSERT INTO `chatrooms`.`inTopic` (`id_user`, `id_chatroom`) ";
		query += "VALUES (?,?)";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1, id_user);
			sentence.setInt(2, id_chatroom);
			sentence.execute();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Añadir un mensaje sin menciones */
	public static void insertMessage(String text, int id_user, int id_chatroom){

		if(conexion == null)
			getConexion();

		String query = "INSERT INTO `chatroom`.`MESSAGE` (`text`, `id_user`,`id_chatroom`) ";
		query += "VALUES (?,?,?)";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1, text);
			sentence.setInt(2, id_user);
			sentence.setInt(3, id_chatroom);
			sentence.execute();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Añadir un mensaje con menciones */
	public static void insertMessage(String text, int id_user, int id_chatroom, int[] ids_mentioned){

		if(conexion == null)
			getConexion();

		String ids_mentionedR = "";

		for(int i = 0; i < ids_mentioned.length; i++){
			if(i != 0) ids_mentionedR += ",";
			ids_mentionedR += ids_mentioned[i];
		}

		String query = "INSERT INTO `chatroom`.`MESSAGE` (`text`, `id_user`,`id_chatroom`, `ids_mentioned`) ";
		query += "VALUES (?,?,?,?)";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1, text);
			sentence.setInt(2, id_user);
			sentence.setInt(3, id_chatroom);
			sentence.setString(4,ids_mentionedR);
			sentence.execute();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	public static void main (String[] args){

		insertUser("Eclipse","java_jdk");

		insertChatroom("Javeros",5);

		insertInTopic(5,4);

		insertMessage("Holita Javeros",5,4);

		int[] ids = {1,4};
		insertMessage("Mañana hay Junta de Escuela",3,2,ids);

	}
	*/

}
