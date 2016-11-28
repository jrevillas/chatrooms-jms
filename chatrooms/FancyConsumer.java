package chatrooms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.ArrayList;

/**
 * Created by jrevillas on 07/11/2016.
 */
public class FancyConsumer implements javax.jms.MessageListener {

    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";

    // cualquier objeto se puede utilizar como un mutex al usarlo en un bloque synchronized
    public static Object mutex = new Object();

    Connection connection;

    public static Session session;
    private static MessageConsumer sibylConsumer;
    public static MessageProducer sibylProducer;
    private static Queue sibylRequests;
    private static Queue sibylResponses;

    private static MessageConsumer topicConsumer;
    public static MessageProducer topicProducer;
    private static Topic topic;

    public static String color(String username) {
        switch (username) {
            case "Revillas":
                return "[@" + RED + "jrevillas" + RESET + "] ";
            case "Clara":
                return "[@" + BLUE + "csanchez" + RESET + "] ";
            case "jruiz":
                return "[@" + YELLOW + "jruiz" + RESET + "] ";
            case "migui":
                return "[@" + CYAN + "mnunez" + RESET + "] ";
            case "dmelero":
                return "[@" + GREEN + "dmelero" + RESET + "] ";
            default:
                return "[@" + PURPLE + username + RESET + "] ";
        }
    }

    public FancyConsumer() {
        try {
            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = myConnFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // CREATE_QUEUE(sibylreqRevillas)
            sibylRequests = session.createQueue("sibylreq" + RenderEngineTest.userHandle);

            // CREATE_QUEUE(sibylresRevillas)
            sibylResponses = session.createQueue("sibylres" + RenderEngineTest.userHandle);

            // CREATE_PRODUCER(sibylreqRevillas)
            sibylProducer = session.createProducer(sibylRequests);

            // CREATE_CONSUMER(sibylresRevillas)
            sibylConsumer = session.createConsumer(sibylResponses);

            //               ----------------
            // user1 -SEND-> sibylreqRevillas -RECV-> sibyl
            //               ----------------

            //               ----------------
            // sibyl -SEND-> sibylresRevillas -RECV-> user1
            //               ----------------

            topic = session.createTopic("topic1");
            topicConsumer = session.createConsumer(topic);
            topicProducer = session.createProducer(topic);
            topicConsumer.setMessageListener(this);

            sibylConsumer.setMessageListener(this);
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message msg) {
        try {
            MapMessage mapMsg = (MapMessage) msg;
            if (mapMsg.getJMSDestination() instanceof Topic) {
                if (mapMsg.getInt("TYPE") == MessageType.MSG_SIMPLE.ordinal() || mapMsg.getInt("TYPE") == MessageType.MSG_WITH_MENTIONS.ordinal()) {
                    RenderEngine.addMessage(color(mapMsg.getString("USER")) + mapMsg.getString("CONTENT"));
                }
            }

            if (mapMsg.getJMSDestination() instanceof Queue) {
                if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CREATE.ordinal()) {
                    RenderEngine.addTopic(mapMsg.getString("CHATROOM"));
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_NEW_MESSAGE.ordinal()) {
                    String chatroom = mapMsg.getString("CHATROOM");
                    // Si la notificación se refiere a la sala en la que estás, no hagas nada.
                    if (chatroom.equals(RenderEngineTest.userChatroom)) {
                        return;
                    } else {
                        RenderEngine.notify(chatroom);
                    }
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_USER_JOIN_ROOM.ordinal()) {
                    // System.out.println("TOPIC PARA SUSCRIBIRSE: " + mapMsg.getString("TOPIC"));
                    RenderEngineTest.userChatroom = mapMsg.getString("CHATROOM");
                    topicConsumer.close();
                    topicProducer.close();
                    topic = session.createTopic(mapMsg.getString("TOPIC"));
                    topicConsumer = session.createConsumer(topic);
                    topicProducer = session.createProducer(topic);
                    topicConsumer.setMessageListener(this);

                    // limpiar contador de notificaciones para la nueva sala
                    for (RenderEngine.TopicWithMessages t : RenderEngine.getTopics()) {
                        if (t.name.equals(mapMsg.getString("CHATROOM"))) {
                            t.messages = 0;
                            break;
                        }
                    }

                    RenderEngine.setMessages(new ArrayList<String>());

                    String lobbyMessagesAsString = mapMsg.getString("CONTENT");
                    // System.out.println("CONTENT - " + lobbyMessagesAsString);
                    String[] lobbyMessages = lobbyMessagesAsString.split("\\|");

                    // TODO con la notación de Migui ahora tenemos el problema de que no enviamos
                    //      el nombre del usuario: msg1|msg2|msg3

                    // Si la vida te devuelve limones, habrá que hacer limonada.
                    for (int i = lobbyMessages.length - 1; i >= 4; i--) {
                        RenderEngine.addMessage(lobbyMessages[i]);
                    }
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CHANGE_NAME.ordinal()) {
                    for (int i = 0; i < RenderEngine.getTopics().size(); i++) {
                        if (RenderEngine.getTopics().get(i).name.equals(mapMsg.getString("CHATROOM"))) {
                            RenderEngine.getTopics().get(i).name = mapMsg.getString("NEW");
                            break;
                        }
                    }
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_NEW_MENTION.ordinal()) {
                    String chatroom = mapMsg.getString("CHATROOM");
                    // Si la notificación se refiere a la sala en la que estás, no hagas nada.
                    if (chatroom.equals(RenderEngineTest.userChatroom)) {
                        return;
                    } else {
                        RenderEngine.notifyMention(chatroom);
                    }
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_LOGIN.ordinal() && mapMsg.getBoolean("STATUS")) {

                    RenderEngineTest.goodLogin = true;

                    String topicsAsString = mapMsg.getString("TOPICS");
                    // System.out.println("TOPICS - " + topicsAsString);
                    String[] topics = topicsAsString.split("\\|");
                    for (String topic : topics) {
                        RenderEngine.addTopic(topic);
                    }

                    String lobbyMessagesAsString = mapMsg.getString("CONTENT");
                    // System.out.println("CONTENT - " + lobbyMessagesAsString);
                    String[] lobbyMessages = lobbyMessagesAsString.split("\\|");
                    for (String message : lobbyMessages) {
                        RenderEngine.addMessage(message);
                    }

                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_LOGIN.ordinal() && !mapMsg.getBoolean("STATUS")) {
                    RenderEngine.addMessage("[" + RED + "sibyl" + RESET + "] " + RED + "Autenticación incorrecta." + RESET);
                }
            }

            System.out.print("\n");
            synchronized (mutex) {
                RenderEngine.render();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FancyConsumer();
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
