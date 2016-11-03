package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.BCrypt;

/**
 * Created by jruiz on 10/26/16.
 */
public class Database {

    private static Connection connection = null;
    private static final int BCRYPT_COST = 10;

    static {
        String host = "rpi.ruizcalle.com";
        String usr = "root";
        String passwd = "sandsand"/**/;

        // Obtener conexion
        String driver = "com.mysql.jdbc.Driver";
        String port = "3306";
        String bd = "chatrooms";

        try {
            Class.forName(driver);
            String url = "jdbc:mysql://" + host + ":" + port + "/" + bd;
            connection = DriverManager.getConnection(url, usr, passwd);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // INSERT
    public static void insertUser(User user) {
        String query = "INSERT INTO `chatrooms`.`USER` (`handle`, `password`) ";
        query += "VALUES (?,?)";
        String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(BCRYPT_COST));
        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setString(2, hash);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertChatroom(Chatroom chatroom, User user) {
        String query = "INSERT INTO `chatrooms`.`CHATROOM` (`name`, `handle_creator`) ";
        query += "VALUES (?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            sentence.setString(2, user.getHandle());
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSuscription(User user, Chatroom chatroom) {

        String query = "INSERT INTO `chatrooms`.`suscription` (`handle_user`, `name_chatroom`) ";
        query += "VALUES (?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setString(2, chatroom.getName());
            sentence.execute();
            sentence.close();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void insertMessage(Message message, User user, Chatroom chatroom) {

        String query = "INSERT INTO `chatrooms`.`MESSAGE` (`text`, `handle_user`,`name_chatroom`) ";
        query += "VALUES (?,?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, message.getText());
            sentence.setString(2, user.getHandle());
            sentence.setString(3, chatroom.getName());
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertMessage(Message message, User user, Chatroom chatroom, int[] ids_mentioned) {

        String ids_mentionedR = "";

        for (int i = 0; i < ids_mentioned.length; i++) {
            if (i != 0) ids_mentionedR += ",";
            ids_mentionedR += ids_mentioned[i];
        }

        String query = "INSERT INTO `chatroom`.`MESSAGE` (`text`, `handle_name`,`name_chatroom`, `ids_mentioned`) ";
        query += "VALUES (?,?,?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, message.getText());
            sentence.setString(2, user.getHandle());
            sentence.setString(3, chatroom.getName());
            sentence.setString(4, ids_mentionedR);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public static void deleteSuscription(User user, Chatroom chatroom) {

        String query = "DELETE FROM `chatrooms`.`suscription` ";
        query += "WHERE `handle_user` = ? ";
        query += "AND `name_chatroom`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setString(2, chatroom.getName());
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public static void updatePassword(User user) {

        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `password` = ? ";
        query += "WHERE `handle` = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getPassword());
            sentence.setString(2, user.getHandle());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDate_Status(User user) {

        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `last_conexion`= ?, `status`= ? ";
        query += "WHERE `handle`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setTimestamp(1, user.getLast_conexion());
            sentence.setInt(2, user.getState());
            sentence.setString(3, user.getHandle());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStatus(User user) {

        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `status`= ? ";
        query += "WHERE `handle`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, user.getState());
            sentence.setString(2, user.getHandle());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCurrentTopic(User user, Chatroom chatroom) {

        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `current_topic`= ? ";
        query += "WHERE `handle`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            sentence.setString(2, user.getHandle());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateName(Chatroom chatroom, String name) {

        String query = "UPDATE `chatrooms`.`CHATROOM` ";
        query += "SET `name`= ? ";
        query += "WHERE `name`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, name);
            sentence.setString(2, chatroom.getName());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateHandle(User user, String handle){

        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `handle`= ? ";
        query += "WHERE `handle`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, handle);
            sentence.setString(2, user.getHandle());
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // QUERIES
    public static Chatroom getChatroom(Chatroom chatroom){
        Chatroom result = new Chatroom();

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";
        query += "WHERE name = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1,chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            rs.next();

            result.setName(rs.getString("name"));
            result.setHandle_creator(rs.getString("handle_creator"));
            result.setCreate_date(rs.getTimestamp("create_date"));

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static User getUser(User user){
        User result = new User();

        String query = "SELECT * ";
        query += "FROM chatrooms.USER ";
        query += "WHERE handle = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1,user.getHandle());
            ResultSet rs = sentence.executeQuery();

            rs.next();

            result.setHandle(rs.getString("handle"));
            result.setPassword(rs.getString("password"));
            result.setLast_conexion(rs.getTimestamp("last_conexion"));
            result.setCurrent_topic(rs.getString("current_topic"));
            result.setState(rs.getInt("status"));

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static User[] getUsers() {
        User[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.USER ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            result = new User[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                User user = new User();

                user.setHandle(rs.getString("handle"));
                user.setPassword(rs.getString("password"));
                user.setLast_conexion(rs.getTimestamp("last_conexion"));
                user.setCurrent_topic(rs.getString("current_topic"));
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

    public static User[] getUsersFromChatroom(Chatroom chatroom) {
        User[] result = null;

        String query = "SELECT u.handle, u.password, u.last_conexion, u.current_topic, u.status ";
        query += "FROM chatrooms.suscription s, chatrooms.USER u ";
        query += "WHERE u.handle = s.handle_user ";
        query += "AND s.name_chatroom = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1,chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            result = new User[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                User user = new User();

                user.setHandle(rs.getString("handle"));
                user.setPassword(rs.getString("password"));
                user.setLast_conexion(rs.getTimestamp("last_conexion"));
                user.setCurrent_topic(rs.getString("current_topic"));
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

    public static Chatroom[] getChatrooms() {
        Chatroom[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            result = new Chatroom[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Chatroom chatroom = new Chatroom();

                chatroom.setName(rs.getString("name"));
                chatroom.setHandle_creator(rs.getString("handle_creator"));
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

    public static Chatroom[] getSuscriptionFromUser(User user) {

        Chatroom[] result = null;

        String query = "SELECT c.name, c.handle_creator, c.create_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.suscription s ";
        query += "WHERE u.handle = ? ";
        query += "AND u.handle = s.handle_user ";
        query += "AND s.name_chatroom = c.name ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new Chatroom[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Chatroom chatroom = new Chatroom();

                chatroom.setName(rs.getString("name"));
                chatroom.setHandle_creator(rs.getString("handle_creator"));
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

    public static User[] getSuscriptionFromChatroom(Chatroom chatroom) {

        User[] result = null;

        String query = "SELECT u.handle, u.password, u.last_conexion, u.current_topic, u.status ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.suscription s ";
        query += "WHERE c.name = ? ";
        query += "AND u.handle = s.handle_user ";
        query += "AND s.name_chatroom = c.name ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1,chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            result = new User[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                User user = new User();

                user.setHandle(rs.getString("handle"));
                user.setPassword(rs.getString("password"));
                user.setLast_conexion(rs.getTimestamp("last_conexion"));
                user.setCurrent_topic(rs.getString("current_topic"));
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

    public static String getHash(User user) {

        String result = "";

        String query = "SELECT u.password ";
        query += "FROM chatrooms.USER u ";
        query += "WHERE u.handle = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            rs.next();

            result = rs.getString("password");

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Message[] getMessages() {
        Message[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMessagesFromUser(User user) {

        Message[] result = null;

        String query = "SELECT m.id, m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMessagesFromChatroom(Chatroom chatroom) {

        Message[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m ";
        query += "WHERE c.name = m.name_chatroom ";
        query += "AND c.name = ? ";
        query += "LIMIT 20";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMessagesInChatroomByUser(User user, Chatroom chatroom) {

        Message[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE c.name = m.name_chatroom ";
        query += "AND u.handle = ? ";
        query += "AND c.name = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setString(2, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMentionedMessages() {
        Statement sentence = null;
        Message[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE m ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";

        try {
            sentence = connection.createStatement();
            ResultSet rs = sentence.executeQuery(query);

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getUserMentions(User user) {

        Message[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMentionsToUserAll(User user) {

        Message[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.handle,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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

    public static Message[] getMentionsToUser(User user, Chatroom chatroom) {

        Message[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND c.name = ? ";
        query += "AND c.name = m.name_chatroom ";
        query += "AND u.handle = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.handle,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            sentence.setString(2, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                Message message = new Message();

                message.setText(rs.getString("text"));
                message.setHandle_user(rs.getString("handle_user"));
                message.setName_chatroom(rs.getString("name_chatroom"));
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
}
