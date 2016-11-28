package sibyl;

import database.Chatroom;
import database.Database;
import database.StdMessage;
import database.User;

import javax.jms.*;

/**
 * Created by jruiz on 11/7/16.
 */
public class SibylQueueManager implements javax.jms.MessageListener {

    public static String lobbyName = Database.getChatroomById(1).getName();
    private static final String ROCKET = "\uD83D\uDE80";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    // Solo llegamos aquí cuando un usuario se comunica con Sibyl
    public SibylQueueManager() {

    }

    public void onMessage(Message msg) {
        try {
            System.out.println("Acaba de entrar un mensaje por SibylQueueManager");
            MapMessage message = (MapMessage) msg;
            int type = message.getInt("TYPE");

            switch (Types.values()[type]) {
                case REQ_USER_JOIN_ROOM:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        System.out.println(printError("REQ_USER_JOIN_ROOM"));
                        break;
                    }
                    REQ_USER_JOIN_ROOM(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_USER_LEAVE_ROOM:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        System.out.println(printError("REQ_USER_LEAVE_ROOM"));
                        break;
                    }
                    REQ_USER_LEAVE_ROOM(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_ROOM_CREATE:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        System.out.println(printError("REQ_ROOM_CREATE"));
                        break;
                    }
                    REQ_ROOM_CREATE(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_USER_CHANGE_PASSWORD:
                    if (message.getString("USER") == null || message.getString("PASSWD") == null) {
                        System.out.println(printError("REQ_USER_CHANGE_PASSWORD"));
                        break;
                    }
                    REQ_USER_CHANGE_PASSWORD(message.getString("USER"), message.getString("PASSWD"));
                    break;
                case REQ_ROOM_CHANGE_NAME:
                    if (message.getString("CHATROOM") == null || message.getString("NEW") == null || message.getString("USER") == null) {
                        System.out.println(printError("REQ_ROOM_CHANGE_NAME"));
                        break;
                    }
                    REQ_ROOM_CHANGE_NAME(message.getString("CHATROOM"), message.getString("NEW"), message.getString("USER"));
                    break;
                case REQ_LOGIN:
                    if (message.getString("USER") == null || message.getString("PASSWORD") == null) {
                        System.out.println(printError("REQ_LOGIN"));
                        break;
                    }
                    System.out.println("[REQ_LOGIN] USER:" + message.getString("USER") + " PASSWORD:" + message.getString("PASSWORD"));
                    REQ_LOGIN(message.getString("USER"), message.getString("PASSWORD"));
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String printError(String messageType) {
        String result = "[" + messageType + "] " + RED + "Mensaje deforme" + RESET;
        return result;
    }

    private void REQ_LOGIN(String name, String password) {
        User user_login = new User();
        user_login.setHandle(name);
        user_login.setPassword(password);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            if (!BotLogic.login(user_login)) {
                toSend.setInt("TYPE", Types.RES_LOGIN.ordinal());
                toSend.setBoolean("STATUS", false);
                // printMap();
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
                toSend.setString("CONTENT", RES_LAST_MESSAGES(chatrooms_array[0].getName()));
                //TODO: INSERTAR USUARIO EN EL MAPA CON LAS USER CONNECTION
                if (Launcher.map.get(user_login.getHandle()) == null) {
                    chatroomConnectionCreation(user_login.getHandle());
                }
                // printMap();
                //
                // System.out.println("TOPICS: " + toSend.getString("TOPICS") + "\n\tCONTENT: " + toSend.getString("CONTENT"));
                Launcher.map.get(user_login.getHandle()).getSibylProducerM().send(toSend);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void printMap () {
        for (String each: Launcher.map.keySet()) {
            System.out.println("[USER]: " + Launcher.map.get(each).getHandle());
        }
    }

    private void chatroomConnectionCreation(String name) {
        UserConnection userConnection = new UserConnection();
        try {
            Queue sibylQueueReq = Launcher.subSession.createQueue("sibylreq" + name);
            Queue sibylQueueRes = Launcher.subSession.createQueue("sibylres" + name);

            MessageConsumer consumer = Launcher.subSession.createConsumer(sibylQueueReq);
            MessageProducer producer = Launcher.subSession.createProducer(sibylQueueRes);

            userConnection.setHandle(name);
            userConnection.setSibylConsumerM(consumer);
            userConnection.setSibylProducerM(producer);
            userConnection.setSibylReqQ(sibylQueueReq);
            userConnection.setSibylResQ(sibylQueueRes);
            consumer.setMessageListener(new SibylQueueManager());
            Launcher.map.put(name, userConnection);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void REQ_USER_JOIN_ROOM(String handle, String name) {
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.join(user, chatroom);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            String content = RES_LAST_MESSAGES(chatroom.getName());
            toSend.setInt("TYPE", Types.RES_USER_JOIN_ROOM.ordinal());
            toSend.setString("CONTENT", content);
            toSend.setString("TOPIC", Launcher.topicMap.get(name).getTopic().getTopicName());
            toSend.setString("CHATROOM", name);
            System.out.println("[CONTENT]: " + toSend.getString("CONTENT") + " [CHATROOM]: " + toSend.getString("CHATROOM"));
            Launcher.map.get(user.getHandle()).getSibylProducerM().send(toSend);

            MessageProducer msgProducer = Launcher.topicMap.get(name).getTopicProducer();
            MapMessage toTopic = Launcher.subSession.createMapMessage();
            toTopic.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
            toTopic.setString("CHATROOM", name);
            toTopic.setString("CONTENT", "El usuario " + user.getHandle() + " ha entrado en la sala.");
            toTopic.setString("USER", "sibylbot");
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

        // chatroom.setName(lobbyName);

        // REQ_USER_JOIN_ROOM(handle, name);
        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_USER_LEAVE_ROOM.ordinal());
            toSend.setBoolean("STATUS", true);
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

        try {
            if (BotLogic.changePasswd(user, passwd)) {
                MapMessage toSend = Launcher.subSession.createMapMessage();
                toSend.setInt("TYPE", Types.RES_USER_CHANGE_PASSWORD.ordinal());
                toSend.setBoolean("STATUS", true);
                Launcher.map.get(handle).getSibylProducerM().send(toSend);
            } else {
                MapMessage toSend = Launcher.subSession.createMapMessage();
                toSend.setInt("TYPE", Types.RES_USER_CHANGE_PASSWORD.ordinal());
                toSend.setBoolean("STATUS", false);
                Launcher.map.get(handle).getSibylProducerM().send(toSend);
            }
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

        lobbyName = newName;

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
            toTopic.setString("CONTENT", "[\u001B[34msibyl-ai\u001B[0m] " + ROCKET + "  The user " + user +
                    " has changed the name of " + name + " to: " + Launcher.topicMap.get(newName).getTopic().getTopicName());
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

        if (arrayMessages.length == 0) {
            return result;
        }

        for (int i = 0; i < arrayMessages.length - 1; i++) {
            result += arrayMessages[i].getText() + "|";
        }
        System.out.println("LEEEEENGTH: " + arrayMessages.length);
        result += result + arrayMessages[arrayMessages.length - 1].getText();
        return result;
    }
}