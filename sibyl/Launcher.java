package sibyl;

import database.Chatroom;
import database.StdMessage;
import database.User;

import javax.jms.*;


public class Launcher implements javax.jms.MessageListener {

    // Definimos las diferentes acciones, lo ponemos como string, solo deja así las claves
    // Luego veremos si se puede hacer con una clase enumeración
    private static final int MSG_JOIN = 0;
    private static final int MSG_LEAVE = 1;
    private static final int MSG_CREATE = 2;
    private static final int MSG_UPDATE_PASSWD = 3;
    private static final int MSG_CH_CHATROOM = 4;
    private static final int MSG = 5;
    private static final int MSG_MENTIONS = 6;
    private static final int MSG_LAST = 7;

    private static ConnectionFactory myConnFactory;
    private static Connection myConn;
    private static Session subSession;
    private static Topic chatTopic;
    private static Queue sibylQueue;


    public Launcher() {
        try {
            myConnFactory = new com.sun.messaging.ConnectionFactory();
            myConn = myConnFactory.createConnection();
            subSession = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            chatTopic = subSession.createTopic("clase");

            sibylQueue = subSession.createQueue("sibyl");

            MessageConsumer sibylSub = subSession.createConsumer(sibylQueue);
            MessageConsumer subscriber = subSession.createConsumer(chatTopic);
            subscriber.setMessageListener(this);
            sibylSub.setMessageListener(this);
            myConn.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message msg) {
        System.out.println("Rim Rim!!!");
        // TODO: REFACTOR
        try {
            MapMessage message = (MapMessage) msg;
            int type = message.getInt("MSG_TYPE");
            switch (type) {
                case MSG_JOIN:
                    MSG_JOIN(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case MSG_LEAVE:
                    MSG_LEAVE(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case MSG_CREATE:
                    MSG_CREATE(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case MSG_UPDATE_PASSWD:
                    MSG_UPDATE_PASSWD(message.getString("USER"), message.getString("PASSWD"));
                    break;
                case MSG_CH_CHATROOM:
                    MSG_CH_CHATROOM(message.getString("CHATROOM"), message.getString("NEW"));
                    break;
                case MSG:
                    MSG(message.getString("MSG_CONTENT"), message.getString("USER"),
                            message.getString("CHATROOM"));
                    break;
                case MSG_MENTIONS:
                    MSG_MENTIONS(message.getString("MENTIONS"), message.getString("MSG_CONTENT"),
                            message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case MSG_LAST:
                    MSG_LAST(message.getString("CHATROOM"));
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private void MSG_JOIN (String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.join(user, chatroom);
    }

    private void MSG_LEAVE (String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.leave(user, chatroom);
    }

    private void MSG_CREATE (String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.create(user, chatroom);

        try {
            MapMessage toSend = subSession.createMapMessage();
            MessageProducer producer = subSession.createProducer(sibylQueue);
            toSend.setInt("MSG_TYPE", MSG_CREATE);
            toSend.setString("CHATROOM", chatroom.getName());
            producer.send(toSend);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private void MSG_UPDATE_PASSWD (String handle, String passwd) {
        User user = new User();
        user.setHandle(handle);
        BotLogic.changePasswd(user, passwd);
    }

    private void MSG_CH_CHATROOM(String name, String newName) {
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.changeChatroomName(chatroom, newName);

        try {
            MapMessage toSend = subSession.createMapMessage();
            MessageProducer producer = subSession.createProducer(sibylQueue);
            toSend.setInt("MSG_TYPE", MSG_CH_CHATROOM);
            toSend.setString("CHATROOM", chatroom.getName());
            toSend.setString("NEW", newName);
            producer.send(toSend);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private void MSG (String msgContent, String handle, String name) {
        StdMessage stdMessage = new StdMessage();
        stdMessage.setText(msgContent);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.insertMessage(stdMessage, user, chatroom);
    }

    private void MSG_MENTIONS (String msgMentions, String msgContent, String handle, String name) {
        StdMessage stdMessage = new StdMessage();
        stdMessage.setText(msgContent);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom =  new Chatroom();
        chatroom.setName(name);
        BotLogic.insertMessageMentions(msgMentions, stdMessage, user, chatroom);
    }

    private void MSG_LAST(String name) {
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        String result = "";
        StdMessage[] arrayMessages = BotLogic.getMessagesFromChatroom(chatroom);
        for (int i = 0; i < arrayMessages.length - 1; i++) {
            result += arrayMessages[i].getText() + "|";
        }
        result += result + arrayMessages[arrayMessages.length - 1].getText();

        try {
            MapMessage toSend = subSession.createMapMessage();
            MessageProducer producer = subSession.createProducer(sibylQueue);
            toSend.setInt("MSG_TYPE", MSG_LAST);
            toSend.setString("MSG_CONTENT", result);
            producer.send(toSend);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
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