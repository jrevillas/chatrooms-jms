package sibyl;

import database.Chatroom;
import database.Database;
import database.StdMessage;
import database.User;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;


public class Launcher implements javax.jms.MessageListener {

    private static final String SOURCE = "sibyl.Launcher";


    public static Map<String, UserConnection> map;
    public static Map<String, ChatroomConnection> topicMap;
    private static ConnectionFactory myConnFactory;
    private static Connection myConn;
    public static Session subSession;
    private static Queue login;

    public Launcher() {
        try {
            myConnFactory = new com.sun.messaging.ConnectionFactory();
            myConn = myConnFactory.createConnection();
            subSession = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            map = new HashMap<String, UserConnection>();

            User[] arrayUsers = Database.getUsers();

            PTLogger.info(SOURCE, "Inicializando el HashMap con las conexiones de usuario");

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

                consumer.setMessageListener(new SibylQueueManager());

                map.put(user, userConnection);
            }

            topicMap = new HashMap<String, ChatroomConnection>();
            Chatroom[] chatrooms = Database.getChatrooms();

            PTLogger.info(SOURCE, "Inicializando HashMap con los topics disponibles hasta ahora");

            for (int i = 0; i < chatrooms.length; i++) {
                ChatroomConnection chatroomConnection = new ChatroomConnection();

                int numberDb = i + 1;

                PTLogger.jms(SOURCE, "Indexando el topic " + chatrooms[i].getName()
                        + " que se corresponde con el destino jms: topic" + numberDb);
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

            PTLogger.jms(SOURCE, "Creando cola de login");
            login = subSession.createQueue("login");
            MessageConsumer consumerLogin = subSession.createConsumer(login);
            consumerLogin.setMessageListener(new SibylQueueManager());

            myConn.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            MapMessage message = (MapMessage) msg;
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

    private void printError(String messageType) {
        PTLogger.error(SOURCE, "[" + messageType + "] Mensaje deforme");
    }

    private void MSG_SIMPLE(String msgContent, String handle, String name) {
        PTLogger.jms(SOURCE, "Usuario @" + handle + " ha enviado un mensaje a la chatroom " + name
                + "\n                       └ \u001B[35mNotificando a los usuarios por sus queues\u001B[0m");
        StdMessage stdMessage = new StdMessage();
        stdMessage.setText(msgContent);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.insertMessage(stdMessage, user, chatroom);

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
        PTLogger.jms(SOURCE, "El usuario @" + handle + " ha mencionado a " + msgMentions + " en la chatroom " + name
                + "\n                       └ \u001B[35mNotificando a los usuarios por sus queues\u001B[0m");
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
                User user_db = Database.getUser(new User().setHandle(mention));
                if (user_db == null)
                    continue;
                map.get(mention).getSibylProducerM().send(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sendMessages(Chatroom chatroom, Message message) {
        int id = Database.getChatroomId(chatroom.getName());
        chatroom.setId(id);
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
        PTLogger.jms(SOURCE, "Arrancando servidor sibyl...");
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