package services;

import java.sql.*;

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
        String passwd = "sandsand";

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
        System.out.println("HOLITA FROOM INSERT USER");
    }

    public static void insertChatroom(Chatroom chatroom, User user) {
        String query = "INSERT INTO `chatrooms`.`CHATROOM` (`name`, `id_creator`) ";
        query += "VALUES (?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            sentence.setInt(2, user.getId());
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertInTopic(User user, Chatroom chatroom) {

        String query = "INSERT INTO `chatrooms`.`inTopic` (`id_user`, `id_chatroom`) ";
        query += "VALUES (?,?)";

        int id_user = getUserId(user);
        int id_chatroom = getChatroomId(chatroom);
        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id_user);
            sentence.setInt(2, id_chatroom);
            sentence.execute();
            sentence.close();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static int getUserId (User user) {
        String query = "SELECT USER.id FROM chatrooms.USER WHERE USER.handle = ?";
        int result = 0;
        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            while (rs.next()) {
                result = rs.getInt(1);
            }
            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getChatroomId (Chatroom chatroom) {
        String query = "SELECT CHATROOM.id FROM chatrooms.CHATROOM WHERE CHATROOM.name = ?";
        int result = 0;
        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void insertMessage(Message message, User user, Chatroom chatroom) {
        String text = message.getText();
        int id_user = user.getId();
        int id_chatroom = chatroom.getId();
        String query = "INSERT INTO `chatrooms`.`MESSAGE` (`text`, `id_user`,`id_chatroom`) ";
        query += "VALUES (?,?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, text);
            sentence.setInt(2, id_user);
            sentence.setInt(3, id_chatroom);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertMessage(Message message, User user, Chatroom chatroom, int[] ids_mentioned) {
        String ids_mentionedR = "";
        String text = message.getText();
        int id_user = user.getId();
        int id_chatroom = chatroom.getId();
        for (int i = 0; i < ids_mentioned.length; i++) {
            if (i != 0) ids_mentionedR += ",";
            ids_mentionedR += ids_mentioned[i];
        }

        String query = "INSERT INTO `chatroom`.`MESSAGE` (`text`, `id_user`,`id_chatroom`, `ids_mentioned`) ";
        query += "VALUES (?,?,?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, text);
            sentence.setInt(2, id_user);
            sentence.setInt(3, id_chatroom);
            sentence.setString(4, ids_mentionedR);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public static void deleteInTopic(User user, Chatroom chatroom) {
        int id_user = user.getId();
        int id_chatroom = chatroom.getId();
        String query = "DELETE FROM `chatrooms`.`inTopic` ";
        query += "WHERE `id_user` = ? ";
        query += "AND `id_chatroom`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id_user);
            sentence.setInt(2, id_chatroom);
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public static void updatePassword(User user) {
        String password = user.getPassword();
        int id_user = user.getId();
        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `password` = ? ";
        query += "WHERE `id` = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, password);
            sentence.setInt(2, id_user);
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDate_Status(User user) {
        Timestamp last_connection = user.getLast_conexion();
        int status = user.getState();
        int id_user = user.getId();
        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `last_conexion`= ?, `status`= ? ";
        query += "WHERE `id`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setTimestamp(1, last_connection);
            sentence.setInt(2, status);
            sentence.setInt(3, id_user);
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStatus(User user) {
        int status = user.getState();
        int id_user = user.getId();
        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `status`= ? ";
        query += "WHERE `id`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, status);
            sentence.setInt(2, id_user);
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCurrentTopic(User user, Chatroom chatroom) {
        int id_chatroom = chatroom.getId();
        int id_user = user.getId();
        String query = "UPDATE `chatrooms`.`USER` ";
        query += "SET `current_topic`= ? ";
        query += "WHERE `id`= ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id_chatroom);
            sentence.setInt(2, id_user);
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateName(Chatroom chatroom) {
        String name = chatroom.getName();
        int id_chatroom = chatroom.getId();
        String query = "UPDATE `chatrooms`.`CHATROOM` ";
        query += "SET `name`= ? ";
        query += "WHERE `id`= ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, name);
            sentence.setInt(2, id_chatroom);
            sentence.executeUpdate();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // QUERIES
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

    public static String getHash(User user) {
        String result = "";
        String handle = user.getHandle();

        String query = "SELECT u.password ";
        query += "FROM chatrooms.USER u ";
        query += "WHERE u.handle = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, handle);
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

    public static Message[] getMessagesFromUser(User user) {
        Message[] result = null;
        int id = user.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.id = m.id_user ";
        query += "AND u.id = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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

    public static Message[] getMessagesFromChatroom(Chatroom chatroom) {
        Message[] result = null;
        int id = chatroom.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m ";
        query += "WHERE c.id = m.id_chatroom ";
        query += "AND c.id = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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

    public static Message[] getMessagesInChatroomByUser(User user, Chatroom chatroom) {
        Message[] result = null;
        int id_user = user.getId();
        int id_chatrooms = chatroom.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE c.id = m.id_chatroom ";
        query += "AND u.id = ? ";
        query += " AND c.id = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id_user);
            sentence.setInt(2, id_chatrooms);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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

    public static Message[] getUserMentions(User user) {
        Message[] result = null;
        int id = user.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.id = m.id_user ";
        query += "AND m.ids_mentioned IS NOT NULL ";
        query += "AND u.id = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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

    public static Message[] getMentionsToUserAll(User user) {
        Message[] result = null;
        int id = user.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND u.id = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.id,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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

    public static Message[] getMentionsToUser(User user, Chatroom chatroom) {
        Message[] result = null;
        int id_user = user.getId();
        int id_chatroom = chatroom.getId();

        String query = "SELECT m.id, m.text, m.id_user, m.id_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND c.id = ? ";
        query += "AND c.id = m.id_chatroom ";
        query += "AND u.id = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.id,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id_chatroom);
            sentence.setInt(2, id_user);
            ResultSet rs = sentence.executeQuery();

            result = new Message[rs.last() ? rs.getRow() : 0];
            int i = 0;

            rs.beforeFirst();

            while (rs.next()) {

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
}
