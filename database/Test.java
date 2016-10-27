package services;

import java.sql.*;
import org.mindrot.BCrypt;

/**
 * Created by jruiz on 10/26/16.
 */
public class Test {

    private static Connection connection = null;

    public static void main (String[] args) {
        // Test en el que rellenamos la base de datos entera desde Java
        // Y luego hacemos pruebas sobre ella.

        String host = "rpi.ruizcalle.com";
        String usr = "root";
        String passwd = "sandsand";

        System.out.println("EMPEZAMOS");
        // Primero los diferentes inserts de los usuarios
        User jruiz = new User();
        jruiz.setHandle("Javier");
        jruiz.setPassword("Holita");
        System.out.println("Holita");
        Database.insertUser(jruiz);
        System.out.println("Voy por INSERT JRUIZ");

        User dmelero = new User();
        dmelero.setHandle("Daniel");
        jruiz.setPassword("Que");
        Database.insertUser(dmelero);
        System.out.println("Voy por INSERT DMELERO");

        User mnunezm = new User();
        mnunezm.setHandle("Miguel");
        mnunezm.setPassword("tal");
        Database.insertUser(mnunezm);
        System.out.println("Voy por INSERT MNUNEZM");

        User jrevillas = new User();
        jrevillas.setHandle("Revillas");
        jrevillas.setPassword("estas");
        Database.insertUser(jrevillas);
        System.out.println("Voy por INSERT JREVILLAS");
        // Insert de una chatroom
        Chatroom chatroom = new Chatroom();
        User creator = new User();
        chatroom.setName("Clash Royale");
        Database.insertChatroom(chatroom, creator);
        System.out.println("CHATROOM CREATED");

        // Insert de inTopic
        Database.insertInTopic(jruiz, chatroom);
        Database.insertInTopic(dmelero, chatroom);
        Database.insertInTopic(mnunezm, chatroom);
        Database.insertInTopic(jrevillas, chatroom);
        System.out.println("INSERTED INTOPIC");

        // Insert messages
        Message message_jruiz = new Message();
        message_jruiz.setId_chatroom(chatroom.getId());
        message_jruiz.setId_user(jruiz.getId());
        message_jruiz.setText("I'm the best");

        Message message_mnunezm = new Message();
        message_mnunezm.setId_chatroom(chatroom.getId());
        message_mnunezm.setId_user(message_mnunezm.getId());
        message_mnunezm.setText("Holita soy Migui");

        Message message_jrevillas = new Message();
        message_jrevillas.setId_chatroom(chatroom.getId());
        message_jrevillas.setId_user(message_jrevillas.getId());
        message_jrevillas.setText("I'm the revillas");

        Message message_dmelero = new Message();
        message_dmelero.setId_chatroom(chatroom.getId());
        message_dmelero.setId_user(message_dmelero.getId());
        message_dmelero.setText("Tiki tiki");

        Database.insertMessage(message_jruiz, jruiz, chatroom);
        Database.insertMessage(message_dmelero, dmelero, chatroom);
        Database.insertMessage(message_mnunezm, mnunezm, chatroom);
        Database.insertMessage(message_jrevillas, jrevillas, chatroom);

        User[] users = Database.getUsers();
        for (int i = 0; i < users.length; i++) {
            System.out.println("User nº [" + i + "] = " + users[i].getPassword());
            if (BCrypt.checkpw("Holita", users[i].getPassword())) {
                System.out.println("Solo funciona para mi usuario = " + users[i].getHandle());
            } else {
                System.out.println("Para mí no funciona = " + users[i].getHandle());
            }
        }
        Chatroom[] chatrooms = Database.getChatrooms();
        for (int i = 0; i < chatrooms.length; i++)
            System.out.println("Chatrooms nº [" + i + "] = " + chatrooms[i].getName());

        Message[] messages = Database.getMessages();
        for (int i = 0; i < messages.length; i++)
            System.out.println("Messages nº [" + i + "] = " + messages[i].getText());
    }

}
