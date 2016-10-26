import java.sql.*;

public class Update {

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

	/* Cambiar la contraseña de un usuario */
	public static void updatePassword(int id_user, String password){

		if(conexion == null)
			getConexion();

		String query = "UPDATE `chatrooms`.`USER` ";
		query += "SET `password` = ? ";
		query += "WHERE `id` = ? ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1, password);
			sentence.setInt(2, id_user);
			sentence.executeUpdate();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Cambiar fecha de la última conexion y del estado */
	public static void updateDate_Status(int id_user, Timestamp last_conexion, int status){

		if(status != 1) return;

		if(conexion == null)
			getConexion();

		String query = "UPDATE `chatrooms`.`USER` ";
		query += "SET `last_conexion`= ?, `status`= ? ";
		query += "WHERE `id`= ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setTimestamp(1,last_conexion);
			sentence.setInt(2,status);
			sentence.setInt(3,id_user);
			sentence.executeUpdate();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Cambiar el estado */
	public static void updateStatus(int id_user, int status){

		if(status != 0) return;

		if(conexion == null)
			getConexion();

		String query = "UPDATE `chatrooms`.`USER` ";
		query += "SET `status`= ? ";
		query += "WHERE `id`= ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1,status);
			sentence.setInt(2,id_user);
			sentence.executeUpdate();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Cambiar el chat actual */
	public static void updateCurrentTopic(int id_user, int id_chatroom){

		if(conexion == null)
			getConexion();

		String query = "UPDATE `chatrooms`.`USER` ";
		query += "SET `current_topic`= ? ";
		query += "WHERE `id`= ?";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setInt(1,id_chatroom);
			sentence.setInt(2,id_user);
			sentence.executeUpdate();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Cambiar el nombre de una chatroom */
	public static void updateName(int id_chatroom, String name){

		if(conexion == null)
			getConexion();

		String query = "UPDATE `chatrooms`.`CHATROOM` ";
		query += "SET `name`= ? ";
		query += "WHERE `id`= ? ";

		try {
			PreparedStatement sentence = conexion.prepareStatement(query);
			sentence.setString(1,name);
			sentence.setInt(2,id_chatroom);
			sentence.executeUpdate();
			sentence.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	public static void main (String[] args){

		updatePassword(1,"nuevaPass");

		java.util.Date date = new java.util.Date();
		updateDate_Status(1,new Timestamp(date.getTime()),1);

		updateStatus(1,0);

		updateCurrentTopic(1,1);

		updateName(1,"Clash of Clans");

	}
	*/
}
