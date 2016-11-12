package sibyl;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * Created by jrevillas on 05/11/2016.
 */
public class DynamicProducer {

    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("ClashRoyale");
            MessageProducer msgProducer = session.createProducer(topic);
            MapMessage message = session.createMapMessage();

            while (true) {
                message = askForMessage(message);
                System.out.println("[\u001B[32mINFO\u001B[0m] Sending message...");
                msgProducer.send(message);
                System.out.println("[\u001B[32mINFO\u001B[0m] Done!");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static MapMessage askForMessage(MapMessage message) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("MSG_TYPE (int): ");
            int msgType = scanner.nextInt();
            String user = "";
            String password = "";
            String chatroom = "";
            String newName = "";
            String msg = "";
            String mentions = "";

            switch (Types.values()[msgType]) {
                case REQ_USER_JOIN_ROOM:
                case REQ_USER_LEAVE_ROOM:
                case REQ_ROOM_CREATE:
                    scanner = new Scanner(System.in);
                    System.out.print("USER (string): ");
                    user = scanner.nextLine();

                    scanner = new Scanner(System.in);
                    System.out.print("CHATROOM (string): ");
                    chatroom = scanner.nextLine();

                    message.setInt("MSG_TYPE", msgType);
                    message.setString("USER", user);
                    message.setString("CHATROOM", chatroom);
                    break;
                case REQ_USER_CHANGE_PASSWORD:
                    scanner = new Scanner(System.in);
                    System.out.print("USER (string): ");
                    user = scanner.nextLine();

                    scanner = new Scanner(System.in);
                    System.out.print("PASSWD (string): ");
                    password = scanner.nextLine();

                    message.setInt("MSG_TYPE", msgType);
                    message.setString("USER", user);
                    message.setString("PASSWD", password);
                    break;
                case REQ_ROOM_CHANGE_NAME:
                    scanner = new Scanner(System.in);
                    System.out.println("CHATROOM (string): ");
                    chatroom = scanner.nextLine();

                    scanner = new Scanner(System.in);
                    System.out.print("NEW NAME (string): ");
                    newName = scanner.nextLine();

                    message.setInt("MSG_TYPE", msgType);
                    message.setString("CHATROOM", chatroom);
                    message.setString("NEW", newName);
                    break;
                case MSG_WITH_MENTIONS:
                    scanner = new Scanner(System.in);
                    System.out.println("MENTIONS (string): ");
                    mentions = scanner.nextLine();
                    message.setString("MENTIONS", mentions);
                case MSG_SIMPLE:
                    System.out.println("HOLITA DESDE LA OPCION 6");
                    scanner = new Scanner(System.in);
                    System.out.println("MSG_CONTENT (string): ");
                    msg = scanner.nextLine();

                    scanner = new Scanner(System.in);
                    System.out.println("USER (string): ");
                    user = scanner.nextLine();

                    scanner = new Scanner(System.in);
                    System.out.println("CHATROOM (string): ");
                    chatroom = scanner.nextLine();

                    message.setInt("MSG_TYPE", msgType);
                    message.setString("MSG_CONTENT", msg);
                    message.setString("USER", user);
                    message.setString("CHATROOM", chatroom);
                    break;
            }


            return message;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
