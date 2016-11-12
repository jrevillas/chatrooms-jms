package sibyl;

import com.sun.deploy.util.StringUtils;
import database.Chatroom;
import database.Database;
import database.StdMessage;
import database.User;

import javax.jms.*;

/**
 * Created by jruiz on 11/7/16.
 */
public class SibylQueueManager implements javax.jms.MessageListener {

    private static final String ROCKET = "\uD83D\uDE80";

    // Solo llegamos aquí cuando un usuario se comunica con Sibyl
    public SibylQueueManager() {

    }

    public void onMessage(Message msg) {
        try {
            System.out.println("Rim Rim SibylQueueManager!!!");
            MapMessage message = (MapMessage) msg;
            int type = message.getInt("TYPE");

            switch (Types.values()[type]) {
                case REQ_USER_JOIN_ROOM:
                    REQ_USER_JOIN_ROOM(message.getString("USER"), message.getString("CHATROOM"), msg);
                    break;
                case REQ_USER_LEAVE_ROOM:
                    REQ_USER_LEAVE_ROOM(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_ROOM_CREATE:
                    REQ_ROOM_CREATE(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_USER_CHANGE_PASSWORD:
                    REQ_USER_CHANGE_PASSWORD(message.getString("USER"), message.getString("PASSWORD"));
                    break;
                case REQ_ROOM_CHANGE_NAME:
                    REQ_ROOM_CHANGE_NAME(message.getString("CHATROOM"), message.getString("NEW"), message.getString("USER"));
                    break;
                case REQ_LOGIN:
                    REQ_LOGIN(message.getString("USER"), message.getString("PASSWORD"));
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_LOGIN(String name, String password) {
        User user_login = new User();
        user_login.setHandle(name);
        user_login.setPassword(password);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            if (!BotLogic.login(user_login)) {
                toSend.setInt("TYPE", Types.RES_LOGIN.ordinal());
                System.out.println("FATAL ERROR: Are you trying to hack us?");
                toSend.setBoolean("STATUS", false);
                Launcher.map.get(user_login.getHandle()).getSibylProducerM().send(toSend);
            } else {
                toSend.setInt("TYPE", Types.RES_LOGIN.ordinal());
                toSend.setBoolean("STATUS", true);
                Chatroom[] chatrooms_array = Database.getChatrooms();
                String chatrooms = "";
                for (int i = 0; i < chatrooms_array.length - 1; i++) {
                    chatrooms += chatrooms_array[i].getName() + "|";
                }
                chatrooms += chatrooms_array[chatrooms_array.length - 1].getName();
                toSend.setString("TOPICS", chatrooms);
                toSend.setString("CONTENT", RES_LAST_MESSAGES("lobby"));
                Launcher.map.get(user_login.getHandle()).getSibylProducerM().send(toSend);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_USER_JOIN_ROOM(String handle, String name, Message msg) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.join(user, chatroom);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_USER_JOIN_ROOM.ordinal());
            toSend.setString("CONTENT", RES_LAST_MESSAGES(chatroom.getName()));
            Launcher.map.get(user.getHandle()).getSibylProducerM().send(toSend);

            ChatroomConnection chatroomConnection = Launcher.topicMap.get(name);
            MessageProducer msgProducer = chatroomConnection.getTopicProducer();
            MapMessage toTopic = Launcher.subSession.createMapMessage();
            toTopic.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
            toTopic.setString("CHATROOM", name);
            toTopic.setString("CONTENT", "[\u001B[34msibylbot\u001B[0m] Un nuevo usuario " + user.getHandle() + " ha entrado en esta sala.");
            toTopic.setString("USER", user.getHandle());
            msgProducer.send(toTopic);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void REQ_USER_LEAVE_ROOM(String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.leave(user, chatroom);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_USER_LEAVE_ROOM.ordinal());
            toSend.setString("CONTENT", RES_LAST_MESSAGES("lobby"));
            Launcher.map.get(handle).getSibylProducerM().send(toSend);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_ROOM_CREATE(String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.create(user, chatroom);

        try {
            setUpChatroomConnection(name);

            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_ROOM_CREATE.ordinal());
            toSend.setString("CHATROOM", name);
            Launcher.map.get(user.getHandle()).getSibylProducerM().send(toSend);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private void setUpChatroomConnection(String nameTopic) {
        try {
            System.out.println("topic" + Database.getChatroomId(nameTopic));
            ChatroomConnection chatroomConnection = new ChatroomConnection();
            Topic chatTopic = Launcher.subSession.createTopic("topic" + Database.getChatroomId(nameTopic));
            chatroomConnection.setTopic(chatTopic);
            MessageConsumer subscriber = Launcher.subSession.createConsumer(chatTopic);
            chatroomConnection.setTopicConsumer(subscriber);
            MessageProducer producer = Launcher.subSession.createProducer(chatTopic);
            chatroomConnection.setTopicProducer(producer);
            Launcher.topicMap.put(nameTopic, chatroomConnection);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_USER_CHANGE_PASSWORD(String handle, String passwd) {
        User user = new User();
        user.setHandle(handle);
        BotLogic.changePasswd(user, passwd);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_USER_CHANGE_PASSWORD.ordinal());
            toSend.setBoolean("STATUS", true);
            Launcher.map.get(handle).getSibylProducerM().send(toSend);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_ROOM_CHANGE_NAME(String name, String newName, String user) {
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        System.out.println("CHATROOM: " + chatroom.getName() +
        "\nNEW NAME: " + newName + "\nUSER: " + user);
        BotLogic.changeChatroomName(chatroom, newName);

        User[] users = Database.getUsers();

        // SWAP
        ChatroomConnection chatTopic = Launcher.topicMap.get(name);
        Launcher.topicMap.remove(name);
        Launcher.topicMap.put(newName, chatTopic);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_ROOM_CHANGE_NAME.ordinal());
            toSend.setString("CHATROOM", chatroom.getName());
            toSend.setString("NEW", newName);
            for (int i = 0; i < users.length; i++) {
                Launcher.map.get(users[i].getHandle()).getSibylProducerM().send(toSend);
            }

            ChatroomConnection chatroomConnection = Launcher.topicMap.get(newName);
            // System.out.println("Enviando broadcast por " + chatroomConnection.getTopic().getTopicName());
            MapMessage toTopic = Launcher.subSession.createMapMessage();
            toTopic.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
            toTopic.setString("USER", "SibylAI");
            toTopic.setString("CHATROOM", newName);
            toTopic.setString("CONTENT", "[\u001B[34msibyl-ai\u001B[0m] " + ROCKET + "  The user " + user + " has changed the name of the chatroom " + name + " to: " + newName
                    + " of the topic: " + Launcher.topicMap.get(newName).getTopic());
            chatroomConnection.getTopicProducer().send(toTopic);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private String RES_LAST_MESSAGES(String name) {
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        String result = "";
        StdMessage[] arrayMessages = BotLogic.getMessagesFromChatroom(chatroom);
        for (int i = 0; i < arrayMessages.length - 1; i++) {
            result += arrayMessages[i].getText() + "|";
        }
        result += result + arrayMessages[arrayMessages.length - 1].getText();
        return result;
    }
}