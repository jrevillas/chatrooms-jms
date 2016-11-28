package sibyl;

import database.Chatroom;
import database.Database;
import database.StdMessage;
import database.User;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;


public class Launcher implements javax.jms.MessageListener {

    public static Map<String, UserConnection> map;
    public static Map<String, ChatroomConnection> topicMap;
    private static ConnectionFactory myConnFactory;
    private static Connection myConn;
    public static Session subSession;
    //private static Queue sibylQueue;

    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public Launcher() {
        try {
            myConnFactory = new com.sun.messaging.ConnectionFactory();
            myConn = myConnFactory.createConnection();
            subSession = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // chatTopic = subSession.createTopic("ClashRoyale");

            map = new HashMap<String, UserConnection>();

            User[] arrayUsers = Database.getUsers();

            for (int i = 0; i < arrayUsers.length; i++) {
                UserConnection userConnection = new UserConnection();

                String user = arrayUsers[i].getHandle();

                Queue sibylQueueReq = subSession.createQueue("sibylreq" + user);
                Queue sibylQueueRes = subSession.createQueue("sibylres" + user);

                MessageConsumer consumer = subSession.createConsumer(sibylQueueReq);
                MessageProducer producer = subSession.createProducer(sibylQueueRes);

                userConnection.setHandle(user);

                userConnection.setSibylConsumerM(consumer);
                userConnection.setSibylProducerM(producer);

                userConnection.setSibylReqQ(sibylQueueReq);
                userConnection.setSibylResQ(sibylQueueRes);

                // Llamaremos a SibylQueueManager cuando nos lleguen cosas por alguna cola
                // Técnicamente, si recibimos cosas por el topic, deberíamos quedarnos aquí
                // Basicamente, si tenemos que comunicarnos de vuelta con el usuario
                // Lo hacemos mediante Sibyl.
                consumer.setMessageListener(new SibylQueueManager());

                map.put(user, userConnection);
            }

            topicMap = new HashMap<String, ChatroomConnection>();
            Chatroom[] chatrooms = Database.getChatrooms();

            for (int i = 0; i < chatrooms.length; i++) {
                ChatroomConnection chatroomConnection = new ChatroomConnection();

                int numberDb = i + 1;
                System.out.println("Indexando topic " + chatrooms[i].getName() + " (topic" + numberDb + ")");
                Topic chatTopic = subSession.createTopic("topic" + numberDb);
                chatroomConnection.setTopic(chatTopic);

                chatroomConnection.setTopicName(chatrooms[i].getName());

                MessageConsumer subscriber = subSession.createConsumer(chatTopic);
                chatroomConnection.setTopicConsumer(subscriber);

                MessageProducer producer = subSession.createProducer(chatTopic);
                chatroomConnection.setTopicProducer(producer);

                // Dejamos el producer en el aire por ahora
                topicMap.put(chatrooms[i].getName(), chatroomConnection);
                subscriber.setMessageListener(this);
            }

            myConn.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            MapMessage message = (MapMessage) msg;
            System.out.println("USER: " + message.getString("USER") + " \n\tand CONTENT: " + message.getString("CONTENT") +
                    "\n\tfrom CHATROOM: " + message.getString("CHATROOM"));
            int type = message.getInt("TYPE");
            switch (Types.values()[type]) {
                case MSG_SIMPLE:
                    if (message.getString("CONTENT") == null || message.getString("USER") == null ||
                            message.getString("CHATROOM") == null) {
                        printError("MSG_SIMPLE");
                        break;
                    }
                    MSG_SIMPLE(message.getString("CONTENT"), message.getString("USER"),
                            message.getString("CHATROOM"));
                    break;
                case MSG_WITH_MENTIONS:
                    if (message.getString("CONTENT") == null || message.getString("USER") == null ||
                            message.getString("CHATROOM") == null || message.getString("MENTIONS") == null) {
                        printError("MSG_WITH_MENTIONS");
                        break;
                    }
                    MSG_WITH_MENTIONS(message.getString("MENTIONS"), message.getString("CONTENT"),
                            message.getString("USER"), message.getString("CHATROOM"));
                    break;

            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private String printError(String messageType) {
        String result = "[" + messageType + "] " + RED + "Mensaje deforme" + RESET;
        return result;
    }

    private void MSG_SIMPLE(String msgContent, String handle, String name) {
        StdMessage stdMessage = new StdMessage();
        stdMessage.setText(msgContent);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.insertMessage(stdMessage, user, chatroom);

        // TODO: Notificar a todos los usuarios que estén subscritos a ese topic por sus colas
        try {
            MapMessage message = subSession.createMapMessage();
            message.setInt("TYPE", Types.RES_NEW_MESSAGE.ordinal());
            message.setString("CHATROOM", chatroom.getName());
            sendMessages(chatroom, message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void MSG_WITH_MENTIONS(String msgMentions, String msgContent, String handle, String name) {
        StdMessage stdMessage = new StdMessage();
        stdMessage.setText(msgContent);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.insertMessageMentions(msgMentions, stdMessage, user, chatroom);
        String[] mentions = msgMentions.split(",");

        try {
            MapMessage message = Launcher.subSession.createMapMessage();
            message.setInt("TYPE", Types.RES_NEW_MENTION.ordinal());
            message.setString("CHATROOM", chatroom.getName());
            for (String mention: mentions) {
                System.out.println("Sending to: " + mention + " from chatroom: " + message.getString("CHATROOM"));
                map.get(mention).getSibylProducerM().send(message);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sendMessages(Chatroom chatroom, Message message) {
        User[] users = Database.getUsersFromChatroom(chatroom);
        try {
            for (int i = 0; i < users.length; i++) {
                map.get(users[i].getHandle()).getSibylProducerM().send(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Launcher instancia = new Launcher();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("error");
            }
        }
    }
}