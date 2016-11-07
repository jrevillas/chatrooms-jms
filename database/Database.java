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

    public static void insertChatroom(User user, Chatroom chatroom) {
//        System.out.println("DATABASE USER: " + user.getHandle());
//        System.out.println("DATABASE CHATROOM NAME: " + chatroom.getName() + " AND ID: " + chatroom.getId());
        String query = "INSERT INTO `chatrooms`.`CHATROOM` (`name`, `handle_creator`) ";
        query += "VALUES (?,?)";
        int last_inserted_id = 0;
        try {
            PreparedStatement sentence = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            sentence.setString(1, chatroom.getName());
            sentence.setString(2, user.getHandle());

            int affected_rows = sentence.executeUpdate();
            ResultSet generatedKeys = sentence.getGeneratedKeys();
            if (generatedKeys.next()) {
                chatroom.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSubscription(User user, Chatroom chatroom) {

        String query = "INSERT INTO `chatrooms`.`SUBSCRIPTION` (`handle_user`, `id_chatroom`) ";
        query += "VALUES (?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setInt(2, chatroom.getId());
            sentence.execute();
            sentence.close();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void insertMessage(StdMessage message, User user, Chatroom chatroom) {

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

    public static void insertMessage(StdMessage message, User user, Chatroom chatroom, String ids_mentioned) {

        String query = "INSERT INTO `chatrooms`.`MESSAGE` (`text`, `handle_user`,`name_chatroom`, `ids_mentioned`) ";
        query += "VALUES (?,?,?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, message.getText());
            sentence.setString(2, user.getHandle());
            sentence.setString(3, chatroom.getName());
            sentence.setString(4, ids_mentioned);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public static void deleteSubscription(User user, Chatroom chatroom) {

        String query = "DELETE FROM `chatrooms`.`SUBSCRIPTION` ";
        query += "WHERE `handle_user` = ? ";
        query += "AND `name_chatroom`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            sentence.setInt(2, chatroom.getId());
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
            sentence.setInt(1, chatroom.getId());
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

    // QUERIES
    public static Chatroom getChatroomByName(Chatroom chatroom){
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

    public static Chatroom findChatroomById (Chatroom chatroom) {
        Chatroom result = new Chatroom();

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";
        query += "WHERE name = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1,chatroom.getId());
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
        query += "FROM chatrooms.SUBSCRIPTION s, chatrooms.USER u ";
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

                chatroom.setId(rs.getInt("id"));
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
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.SUBSCRIPTION s ";
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
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.SUBSCRIPTION s ";
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

    public static StdMessage[] getMessages() {
        StdMessage[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMessagesFromUser(User user) {

        StdMessage[] result = null;

        String query = "SELECT m.id, m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMessagesFromChatroom(Chatroom chatroom) {

        StdMessage[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m ";
        query += "WHERE c.name = m.name_chatroom ";
        query += "AND c.name = ? ";
        query += "LIMIT 5";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMessagesInChatroomByUser(User user, Chatroom chatroom) {

        StdMessage[] result = null;

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

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMentionedMessages() {
        Statement sentence = null;
        StdMessage[] result = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE m ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";

        try {
            sentence = connection.createStatement();
            ResultSet rs = sentence.executeQuery(query);

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getUserMentions(User user) {

        StdMessage[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMentionsToUserAll(User user) {

        StdMessage[] result = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.handle,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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

    public static StdMessage[] getMentionsToUser(User user, Chatroom chatroom) {

        StdMessage[] result = null;

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

            result = new StdMessage[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

                StdMessage message = new StdMessage();

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
