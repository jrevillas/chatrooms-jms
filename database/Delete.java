import java.sql.*;

public class Delete {

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

    /* Eliminar una suscripcion de un chatroom */
    public static void deleteInTopic(int id_user, int id_chatroom){

        if(conexion == null)
            getConexion();

        String query = "DELETE FROM `chatrooms`.`inTopic` ";
        query += "WHERE `id_user` = ? ";
        query += "AND `id_chatroom`= ? ";

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

    /*
    public static void main (String[] args){
        deleteInTopic(4,3);
    }
    */
}
