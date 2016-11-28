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
        User[] users = Database.getsubscriptionFromChatroom(chatroom);
        for (User each : users) {
            if (each.getHandle().equals(user.getHandle()))
                return;
        }
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

    public static boolean changePasswd(User handle, String newPasswd) {
        // Primero llamamos a getUser para tener el objeto usuario entero
        User user = Database.getUser(handle);

        // Asumimos que el usuario está perfectamente logeado
        if (BCrypt.checkpw(newPasswd, user.getPassword())) {
            user.setPassword(BCrypt.hashpw(newPasswd, BCrypt.gensalt(10)));
            Database.updatePassword(user);
            return true;
        }
        return false;
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
        User user_db = Database.getUser(user_login);
        System.out.println("User: " + user_login.getHandle() + " Password: " + user_login.getPassword());
        // Si el usuario no existe, login incorrecto
        // TODO: ya no, ahora si el usuario no existe, se crea

        if (user_db == null) {
            Database.insertUser(user_login);
            System.out.println("Usuario creado con: " + user_login.getHandle());
            // también le ponemos que esté subscrito al topic lobby
            Chatroom chatroom = new Chatroom();
            chatroom.setId(1);
            Database.insertSubscription(user_login, chatroom);
            return true;
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
