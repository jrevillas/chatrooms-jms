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

    private static final String SOURCE = "sibyl.SibylQueueManager";

    public static String lobbyName = Database.getChatroomById(1).getName();
    private static final String ROCKET = "\uD83D\uDE80";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    // Solo llegamos aquí cuando un usuario se comunica con Sibyl
    public SibylQueueManager() {

    }

    public void onMessage(Message msg) {
        try {
            MapMessage message = (MapMessage) msg;
            int type = message.getInt("TYPE");
            switch (Types.values()[type]) {
                case REQ_USER_JOIN_ROOM:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        printError("REQ_USER_JOIN_ROOM");
                        break;
                    }
                    REQ_USER_JOIN_ROOM(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_USER_LEAVE_ROOM:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        printError("REQ_USER_LEAVE_ROOM");
                        break;
                    }
                    REQ_USER_LEAVE_ROOM(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_ROOM_CREATE:
                    if (message.getString("USER") == null || message.getString("CHATROOM") == null) {
                        printError("REQ_ROOM_CREATE");
                        break;
                    }
                    REQ_ROOM_CREATE(message.getString("USER"), message.getString("CHATROOM"));
                    break;
                case REQ_USER_CHANGE_PASSWORD:
                    if (message.getString("USER") == null || message.getString("PASSWD") == null) {
                        printError("REQ_USER_CHANGE_PASSWORD");
                        break;
                    }
                    REQ_USER_CHANGE_PASSWORD(message.getString("USER"), message.getString("PASSWD"));
                    break;
                case REQ_ROOM_CHANGE_NAME:
                    if (message.getString("CHATROOM") == null || message.getString("NAME") == null || message.getString("USER") == null) {
                        printError("REQ_ROOM_CHANGE_NAME");
                        break;
                    }
                    REQ_ROOM_CHANGE_NAME(message.getString("CHATROOM"), message.getString("NAME"), message.getString("USER"));
                    break;
                case REQ_LOGIN:
                    if (message.getString("USER") == null || message.getString("PASSWORD") == null) {
                        printError("REQ_LOGIN");
                        break;
                    }
                    REQ_LOGIN(message.getString("USER"), message.getString("PASSWORD"));
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void printError(String messageType) {
        PTLogger.error(SOURCE, "[" + messageType + "] Mensaje deforme");
    }

    private void REQ_LOGIN(String name, String password) {
        PTLogger.debug(SOURCE, "Recibida petición de login de @" + name);
        User user_login = new User();
        user_login.setHandle(name);
        user_login.setPassword(password);

        try {
            MapMessage toSend = Launcher.subSession.createMapMessage();
            if (!BotLogic.login(user_login)) {
                PTLogger.warn(SOURCE, "Usuario @" + name + " ha introducido mal su contraseña");
                toSend.setInt("TYPE", Types.RES_LOGIN.ordinal());
                toSend.setBoolean("STATUS", false);
                PTLogger.jms(SOURCE, "Respondiendo al usuario @" + name + " por su queue sibylres" + name);
                Launcher.map.get(user_login.getHandle()).getSibylProducerM().send(toSend);
            } else {
                toSend.setInt("TYPE", Types.RES_LOGIN.ordinal());
                toSend.setBoolean("STATUS", true);
                Chatroom[] chatrooms_array = Database.getChatrooms();
                PTLogger.debug(SOURCE, "Login correcto, rellenando lista de chatrooms disponibles");
                String chatrooms = "";
                for (int i = 0; i < chatrooms_array.length - 1; i++) {
                    chatrooms += chatrooms_array[i].getName() + "|";
                }
                chatrooms += chatrooms_array[chatrooms_array.length - 1].getName();
                toSend.setString("TOPICS", chatrooms);
                toSend.setString("CONTENT", RES_LAST_MESSAGES(chatrooms_array[0].getName()));
                if (Launcher.map.get(user_login.getHandle()) == null) {
                    PTLogger.jms(SOURCE, "Añadiendo usuario @" + name + " al HashMap con las conexiones de usuario");
                    chatroomConnectionCreation(user_login.getHandle());
                }
                Launcher.map.get(user_login.getHandle()).getSibylProducerM().send(toSend);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

//    private void printMap () {
//        for (String each: Launcher.map.keySet()) {
//            System.out.println("[USER]: " + Launcher.map.get(each).getHandle());
//        }
//    }

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
        PTLogger.slash(SOURCE, "Usuario @" + handle + " accede a la chatroom: " + name);
        if (Launcher.topicMap.get(name) == null) {
            PTLogger.warn(SOURCE, "Procesando petición durante la fase de arranque, omitiendo...");
            return;
        }

        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.join(user, chatroom);

        try {
            PTLogger.jms(SOURCE, "Enviando los ultimos mensajes al usuario @" + handle + " por su queue sibylres" + handle);
            MapMessage toSend = Launcher.subSession.createMapMessage();
            String content = RES_LAST_MESSAGES(chatroom.getName());
            toSend.setInt("TYPE", Types.RES_USER_JOIN_ROOM.ordinal());
            toSend.setString("CONTENT", content);
            toSend.setString("TOPIC", Launcher.topicMap.get(name).getTopic().getTopicName());
            toSend.setString("CHATROOM", name);
            Launcher.map.get(user.getHandle()).getSibylProducerM().send(toSend);

            PTLogger.jms(SOURCE, "Avisando a todos de que @" + handle + " ha entrado en la chatroom "
                    + name + " (" + Launcher.topicMap.get(name).getTopic().getTopicName() + ")");
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
        PTLogger.slash(SOURCE, "El usuario @" + handle + " quiere salir de la sala " + name);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        BotLogic.leave(user, chatroom);

        try {
            PTLogger.jms(SOURCE, "Confirmando al usuario por su queue");
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_USER_LEAVE_ROOM.ordinal());
            toSend.setBoolean("STATUS", true);
            User user_db = Database.getUser(new User().setHandle(handle));
            if (user_db != null)
                Launcher.map.get(handle).getSibylProducerM().send(toSend);

            PTLogger.jms(SOURCE, "Enviando mensaje por topic a todos los usuarios");
            ChatroomConnection chatroomConnection = Launcher.topicMap.get(name);
            MapMessage toTopic = Launcher.subSession.createMapMessage();
            toTopic.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
            toTopic.setString("USER", "sibylbot");
            toTopic.setString("CHATROOM", name);
            toTopic.setString("CONTENT", "The user " + handle + " has left the chatroom");
            chatroomConnection.getTopicProducer().send(toTopic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void REQ_ROOM_CREATE(String handle, String name) {
        PTLogger.slash(SOURCE, "Usuario @" + handle + " quiere crear una nueva sala: " + name);
        User user = new User();
        user.setHandle(handle);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);

        if (BotLogic.create(user, chatroom)) {
            try {
                PTLogger.info(SOURCE, "Rellenando informacion de la nueva sala en su HashMap");
                setUpChatroomConnection(name);
                MapMessage toSend = Launcher.subSession.createMapMessage();
                toSend.setInt("TYPE", Types.RES_ROOM_CREATE.ordinal());
                toSend.setString("CHATROOM", name);
                toSend.setBoolean("STATUS", true);
                PTLogger.jms(SOURCE, "Respondiendo al usuario @" + handle + " por la queue sibylres" + handle);
                Launcher.map.get(user.getHandle()).getSibylProducerM().send(toSend);
            } catch (JMSException jmse) {
                jmse.printStackTrace();
            }
        }
        else {
            try {
                PTLogger.warn(SOURCE, "Se ha intentado crear la sala " + name + ", pero ya existe");
                PTLogger.jms(SOURCE, "Respondiendo al usuario @" + handle + " por la queue sibylres" + handle);
                MapMessage error = Launcher.subSession.createMapMessage();
                error.setInt("TYPE", Types.RES_ROOM_CREATE.ordinal());
                error.setBoolean("STATUS", false);
                Launcher.map.get(user.getHandle()).getSibylProducerM().send(error);
            } catch (JMSException e) {
                e.printStackTrace();
            }
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
        PTLogger.slash(SOURCE, "El usuario @" + handle + " quiere cambiar su contraseña a: " + passwd);
        User user = new User();
        user.setHandle(handle);

        try {
            if (BotLogic.changePasswd(user, passwd)) {
                PTLogger.info(SOURCE, "Cambio correctamente realizado");
                PTLogger.jms(SOURCE, "Respondiendo al usuario @" + handle + "por su queue: sibylres" + handle);
                MapMessage toSend = Launcher.subSession.createMapMessage();
                toSend.setInt("TYPE", Types.RES_USER_CHANGE_PASSWORD.ordinal());
                toSend.setBoolean("STATUS", true);
                Launcher.map.get(handle).getSibylProducerM().send(toSend);
            } else {
                PTLogger.warn(SOURCE, "Fallo en la autenticación del usuario @" + handle);
                PTLogger.jms(SOURCE, "Enviando respuesta por la queue del usuario sibylres" + handle);
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
        PTLogger.slash(SOURCE, "El usuario @" + user
                + " quiere cambiar el nombre de la sala " + name + " a " + newName);
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);

        BotLogic.changeChatroomName(chatroom, newName);

        User[] users = Database.getUsers();

        // SWAP
        ChatroomConnection chatTopic = Launcher.topicMap.get(name);
        Launcher.topicMap.remove(name);
        Launcher.topicMap.put(newName, chatTopic);

        lobbyName = newName;

        try {
            PTLogger.jms(SOURCE, "Respondiendo a todos los usuarios por sus correspondientes queues");
            MapMessage toSend = Launcher.subSession.createMapMessage();
            toSend.setInt("TYPE", Types.RES_ROOM_CHANGE_NAME.ordinal());
            toSend.setString("CHATROOM", chatroom.getName());
            toSend.setString("NEW", newName);
            for (int i = 0; i < users.length; i++) {
                Launcher.map.get(users[i].getHandle()).getSibylProducerM().send(toSend);
            }

            PTLogger.jms(SOURCE, "Enviando mensaje por topic a todos los usuarios");
            ChatroomConnection chatroomConnection = Launcher.topicMap.get(newName);

            MapMessage toTopic = Launcher.subSession.createMapMessage();
            toTopic.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
            toTopic.setString("USER", "sibylbot");
            toTopic.setString("CHATROOM", newName);
            toTopic.setString("CONTENT", "The user " + user + " has changed the name of "
                    + name + " to: " + newName);
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
            result += arrayMessages[i].getSend_date() + "|" +  arrayMessages[i].getHandle_user() + "|" + arrayMessages[i].getText() + "|";
        }

        result += arrayMessages[arrayMessages.length - 1].getSend_date() + "|" +
                arrayMessages[arrayMessages.length - 1].getHandle_user() + "|" + arrayMessages[arrayMessages.length - 1].getText();
        return result;
    }
}