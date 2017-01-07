package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.BCrypt;

public class Database {

    private static final int BCRYPT_COST = 10;

    private static Connection connection = null;

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
    }

    public static void insertChatroom(User user, Chatroom chatroom) {

        String query = "INSERT INTO `chatrooms`.`CHATROOM` (`name`, `handle_creator`) ";
        query += "VALUES (?,?)";

        try {
            PreparedStatement sentence = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            sentence.setString(1, chatroom.getName());
            sentence.setString(2, user.getHandle());

            sentence.executeUpdate();
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

        String query = "INSERT INTO `chatrooms`.`subscription` (`handle_user`, `id_chatroom`) ";
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

        String query = "DELETE FROM `chatrooms`.`subscription` ";
        query += "WHERE `handle_user` = ? ";
        query += "AND `id_chatroom`= ? ";

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
        query += "WHERE `handle`= ? ";

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
    public static Chatroom[] getChatrooms() {

        Chatroom[] chatroomList = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            chatroomList = getChatroomDB(sentence, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroomList;
    }

    public static int getChatroomId(String name) {

        int chatroomId = 0;

        String query = "SELECT * " +
                "FROM chatrooms.CHATROOM " +
                "WHERE name = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, name);
            ResultSet rs = sentence.executeQuery();

            rs.next();
            chatroomId = rs.getInt("id");

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroomId;
    }

    public static Chatroom getChatroomById(int id) {

        Chatroom chatroom = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";
        query += "WHERE id = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setInt(1, id);
            ResultSet rs = sentence.executeQuery();

            rs.next();

            chatroom = new Chatroom()
                    .setId(rs.getInt("id"))
                    .setName(rs.getString("name"))
                    .setHandle_creator(rs.getString("handle_creator"))
                    .setCreate_date(rs.getTimestamp("create_date"));

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroom;
    }

    public static Chatroom getChatroomByName(Chatroom chatroom) {

        Chatroom chatroomDB = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.CHATROOM ";
        query += "WHERE name = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            rs.next();

            chatroomDB = new Chatroom()
                    .setId(rs.getInt("id"))
                    .setName(rs.getString("name"))
                    .setHandle_creator(rs.getString("handle_creator"))
                    .setCreate_date(rs.getTimestamp("create_date"));

            sentence.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroomDB;
    }

    public static Chatroom[] getsubscriptionFromUser(User user) {

        Chatroom[] chatroomList = null;

        String query = "SELECT c.name, c.handle_creator, c.create_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.subscription s ";
        query += "WHERE u.handle = ? ";
        query += "AND u.handle = s.handle_user ";
        query += "AND s.id_chatroom = c.name ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            chatroomList = getChatroomDB(sentence, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroomList;
    }

    public static StdMessage[] getMessages() {

        StdMessage[] stdMessageList = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMentionedMessages() {

        StdMessage[] stdMessageList = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.MESSAGE m ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMessagesFromChatroom(Chatroom chatroom) {

        StdMessage[] stdMessageList = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.MESSAGE m ";
        query += "WHERE c.name = m.name_chatroom ";
        query += "AND c.name = ? ";
        query += "ORDER BY m.id ASC ";
        query += "LIMIT 20";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMessagesFromUser(User user) {

        StdMessage[] stdMessageList = null;

        String query = "SELECT m.id, m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMessagesInChatroomByUser(User user, Chatroom chatroom) {

        StdMessage[] stdMessageList = null;

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

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMentionsToUser(User user, Chatroom chatroom) {

        StdMessage[] stdMessageList = null;

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

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getMentionsToUserAll(User user) {

        StdMessage[] stdMessageList = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ? ";
        query += "AND m.ids_mentioned LIKE CONCAT('%',u.handle,'%') ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static StdMessage[] getUserMentions(User user) {

        StdMessage[] stdMessageList = null;

        String query = "SELECT m.text, m.handle_user, m.name_chatroom, m.ids_mentioned, m.send_date ";
        query += "FROM chatrooms.MESSAGE m, chatrooms.USER u ";
        query += "WHERE u.handle = m.handle_user ";
        query += "AND m.ids_mentioned IS NOT NULL ";
        query += "AND u.handle = ?";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            stdMessageList = getStdMessageDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList;
    }

    public static User getUser(User user) {

        String query = "SELECT * ";
        query += "FROM chatrooms.USER ";
        query += "WHERE handle = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, user.getHandle());
            ResultSet rs = sentence.executeQuery();

            rs.next();

            User result = new User()
                    .setHandle(rs.getString("handle"))
                    .setPassword(rs.getString("password"))
                    .setLast_conexion(rs.getTimestamp("last_conexion"))
                    .setCurrent_topic(rs.getString("current_topic"))
                    .setState(rs.getInt("status"));

            sentence.close();
            rs.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User[] getsubscriptionFromChatroom(Chatroom chatroom) {

        User[] userList = null;

        String query = "SELECT u.handle, u.password, u.last_conexion, u.current_topic, u.status ";
        query += "FROM chatrooms.CHATROOM c, chatrooms.USER u, chatrooms.subscription s ";
        query += "WHERE c.name = ? ";
        query += "AND u.handle = s.handle_user ";
        query += "AND s.id_chatroom = c.name ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            userList = getUserDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public static User[] getUsers() {

        User[] userList = null;

        String query = "SELECT * ";
        query += "FROM chatrooms.USER ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            ResultSet rs = sentence.executeQuery();

            userList = getUserDB(sentence,rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public static User[] getUsersFromChatroom(Chatroom chatroom) {

        User[] userList = null;

        String query = "SELECT u.handle, u.password, u.last_conexion, u.current_topic, u.status ";
        query += "FROM chatrooms.subscription s, chatrooms.USER u ";
        query += "WHERE u.handle = s.handle_user ";
        query += "AND s.id_chatroom = ? ";

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            sentence.setString(1, chatroom.getName());
            ResultSet rs = sentence.executeQuery();

            userList = getUserDB(sentence, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;

    }

    /**
     * Función auxiliar que devuleve un array con las chatrooms pedidas a partir de una
     * sentecia PreparedStatement y de un ResultSet, que al finalizar la función se cerrarán
     * @param sentence
     * @param resultSet
     * @return Chatroom[]
     */
    private static Chatroom[] getChatroomDB(PreparedStatement sentence, ResultSet resultSet){

        List<Chatroom> chatroomList = new ArrayList<>();

        try {

            while(resultSet.next()){

                Chatroom chatroom = new Chatroom()
                        .setId(resultSet.getInt("id"))
                        .setName(resultSet.getString("name"))
                        .setHandle_creator(resultSet.getString("handle_creator"))
                        .setCreate_date(resultSet.getTimestamp("create_date"));

                chatroomList.add(chatroom);
            }

            sentence.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatroomList.toArray(new Chatroom[chatroomList.size()]);
    }

    /**
     * Función auxiliar que devuleve un array con los mensajes pedidos a partir de una
     * sentecia PreparedStatement y de un ResultSet, que al finalizar la función se cerrarán
     * @param sentence
     * @param resultSet
     * @return StdMessage[]
     */
    private static StdMessage[] getStdMessageDB(PreparedStatement sentence, ResultSet resultSet){

        List<StdMessage> stdMessageList = new ArrayList<>();

        try {

            while (resultSet.next()) {

                StdMessage stdMessage = new StdMessage()
                        .setText(resultSet.getString("text"))
                        .setHandle_user(resultSet.getString("handle_user"))
                        .setName_chatroom(resultSet.getString("name_chatroom"))
                        .setIds_mentioned(resultSet.getString("ids_mentioned"))
                        .setSend_date(resultSet.getTimestamp("send_date"));

                stdMessageList.add(stdMessage);
            }

            sentence.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stdMessageList.toArray(new StdMessage[stdMessageList.size()]);
    }

    /**
     * Función auxiliar que devuleve un array con los usuarios pedidos a partir de una
     * sentecia PreparedStatement y de un ResultSet, que al finalizar la función se cerrarán
     * @param sentence
     * @param resultSet
     * @return StdMessage[]
     */
    private static User[] getUserDB(PreparedStatement sentence, ResultSet resultSet){

        List<User> userList = new ArrayList<>();

        try {

            while (resultSet.next()) {

                User user = new User()
                        .setHandle(resultSet.getString("handle"))
                        .setPassword(resultSet.getString("password"))
                        .setLast_conexion(resultSet.getTimestamp("last_conexion"))
                        .setCurrent_topic(resultSet.getString("current_topic"))
                        .setState(resultSet.getInt("status"));

                userList.add(user);
            }

            sentence.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList.toArray(new User[userList.size()]);

    }

}
