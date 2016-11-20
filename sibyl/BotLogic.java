package sibyl;

import database.*;
import org.mindrot.BCrypt;

import javax.xml.crypto.Data;

/**
 * Created by jruiz on 11/5/16.
 */
public class BotLogic {

    public static void join(User user, Chatroom chatroom) {
        // Llamamos a insertSubscription para la database
        Database.insertSubscription(user, chatroom);
    }

    public static void leave(User user, Chatroom chatroom) {
        // Llamamos a deleteSuscription para la database
        Database.deleteSubscription(user, chatroom);
    }

    public static void create(User user, Chatroom chatroom) {
        // Llamamos a insertChatroom para la database
        Database.insertChatroom(user, chatroom);
    }

    public static void changePasswd(User handle, String newPasswd) {
        // Primero llamamos a getUser para tener el objeto usuario entero
        User user = Database.getUser(handle);

        // Asumimos que el usuario está perfectamente logeado
        user.setPassword(BCrypt.hashpw(newPasswd, BCrypt.gensalt(10)));

        // Suponemos que desde el momento que se pulsa enter, la contraseña se cifra,
        // Es decir, que a este punto, la contraseña nos llega ya cifrada
        Database.updatePassword(user);
    }

    public static void changeChatroomName(Chatroom chatroom, String newName) {
        // Llamamos a updateName para la database
        Database.updateName(chatroom, newName);
    }

    public static void insertMessage(StdMessage text, User user, Chatroom chatroom) {
        // Llamamos a insertMessage para la database
        Database.insertMessage(text, user, chatroom);
    }

    public static void insertMessageMentions(String mentions, StdMessage text, User user, Chatroom chatroom) {
        // Llamamos a insertMessage con las menciones para la database
        Database.insertMessage(text, user, chatroom, mentions);
    }

    public static StdMessage[] getMessagesFromChatroom(Chatroom chatroom) {
        StdMessage[] result = Database.getMessagesFromChatroom(chatroom);
        return result;

    }

    public static Boolean login(User user_login) {
        User user_db = new User();
        user_db = Database.getUser(user_login);

        // Si el usuario no existe, login incorrecto
        if (user_db == null) {
            return false;
        }

        // Comprobamos que los credentials estan bien
        System.out.println("USER_LOGIN -> " + user_login.getPassword());
        System.out.println("USER_LOGIN -> " + user_db.getPassword());
        if (BCrypt.checkpw(user_login.getPassword(), user_db.getPassword())) {
            System.out.println("LOGIN CORRECTO - " + user_login.getHandle());
            return true;
        }
        System.out.println("LOGIN INCORRECTO - " + user_login.getHandle());
        return false;
    }
}
