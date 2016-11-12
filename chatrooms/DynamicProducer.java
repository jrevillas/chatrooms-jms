package chatrooms;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.Topic;

import com.twitter.Extractor;

/**
 * Created by jrevillas on 05/11/2016.
 */
public class DynamicProducer {

    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";
    private static final String ROCKET = "\uD83D\uDE80";

    private static final String BOT_PREFIX = "[@" + BLUE + "superbot" + RESET + "] " + ROCKET + "  ";

    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic("ClashRoyale");
            Queue sibylQueue = session.createQueue("sibylreqRevillas");

            MessageProducer msgProducer = session.createProducer(topic);
            MessageProducer sibylProducer = session.createProducer(sibylQueue);

            MapMessage message = session.createMapMessage();

            while (true) {
                message = askForMessage(message);
                if (message.getInt("TYPE") > 0) {
                    System.out.println("[\u001B[32mINFO\u001B[0m] Sending message to Sibyl...");
                    sibylProducer.send(message);
                } else {
                    System.out.println("[\u001B[32mINFO\u001B[0m] Sending message to \"clase\"...");
                    msgProducer.send(message);
                }

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

            if (msgType == 3) {
                scanner = new Scanner(System.in);
                System.out.print("CHATROOM (string): ");
                String msgContent = scanner.nextLine();
                message.setInt("TYPE", msgType);
                // message.setString("MSG_CONTENT", BOT_PREFIX + msgContent);
                message.setString("USER", "Revillas");
                message.setString("CHATROOM", msgContent);
                return message;
            }

            if (msgType == 2) {
                scanner = new Scanner(System.in);
                System.out.print("CHATROOM (string): ");
                String chatRoomName = scanner.nextLine();

                scanner = new Scanner(System.in);
                System.out.print("NEW (string): ");
                String newName = scanner.nextLine();

                message.setInt("TYPE", msgType);
                // message.setString("MSG_CONTENT", BOT_PREFIX + msgContent);
                message.setString("NEW", newName);
                message.setString("CHATROOM", chatRoomName);
                return message;
            }

            // Mensaje normal
            if (msgType == 5) {
                scanner = new Scanner(System.in);
                System.out.print("MSG_CONTENT (string): ");
                String msgContent = scanner.nextLine();
                message.setInt("MSG_TYPE", msgType);
                message.setString("MSG_CONTENT", BOT_PREFIX + msgContent);
                message.setString("USER", "Revillas");
                message.setString("CHATROOM", "ClashRoyale");
                return message;
            }

            // Mensaje con menciones
            if (msgType == 1) {
                scanner = new Scanner(System.in);
                System.out.print("MSG_CONTENT (string): ");
                String msgContent = scanner.nextLine();

                Extractor extractor = new Extractor();
                List<String> mentions = extractor.extractMentionedScreennames(msgContent);
                String joinedMentions = String.join(",", mentions);

                message.setInt("MSG_TYPE", msgType);
                message.setString("MSG_CONTENT", BOT_PREFIX + msgContent);
                message.setString("MSG_MENTIONS", joinedMentions);
                message.setString("MSG_USER", "Revillas");
                message.setString("MSG_CHATROOM", "ClashRoyale");
                return message;
            }

            if (msgType == 7) {
                scanner = new Scanner(System.in);
                System.out.print("CHATROOM (string): ");
                String msgContent = scanner.nextLine();
                message.setInt("MSG_TYPE", msgType);
                message.setString("USER", "Revillas");
                message.setString("CHATROOM", msgContent);
                return message;
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
