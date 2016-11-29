
package clientGUI;

import chatrooms.MessageType;

import javax.jms.*;

class DynamicProducerGUI {
    private static Session session;
    private static MessageProducer topicProducer;
    private static MessageProducer sibylProducer;

    static void setProducer(String handler) {
        try {
            session = ChatGUI.session;
            Queue sibylQueue = ChatGUI.session.createQueue("sibylreq" + handler);
            sibylProducer = ChatGUI.session.createProducer(sibylQueue);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    static void changeRoom(String chatroom) {
        try {
            if (topicProducer != null)
                topicProducer.close();
            Topic topic = ChatGUI.session.createTopic(chatroom);
            topicProducer = ChatGUI.session.createProducer(topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    static boolean messageLogin(String user, String password) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_LOGIN.ordinal());
            mapMessage.setString("USER", user);
            mapMessage.setString("PASSWORD", password);
            sibylProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageSimple(String chatroom, String content, String mentions, String user) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            if (mentions.length() == 0)
                mapMessage.setInt("TYPE", MessageType.MSG_SIMPLE.ordinal());
            else
                mapMessage.setInt("TYPE", MessageType.MSG_WITH_MENTIONS.ordinal());
            mapMessage.setString("CHATROOM", chatroom);
            mapMessage.setString("CONTENT", content);
            mapMessage.setString("MENTIONS", mentions);
            mapMessage.setString("USER", user);
            topicProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageChangeName(String chatroom, String name, String user) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_ROOM_CHANGE_NAME.ordinal());
            mapMessage.setString("CHATROOM", chatroom);
            mapMessage.setString("NAME", name);
            mapMessage.setString("USER", user);
            sibylProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageRoomCreate(String chatroom, String user) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_ROOM_CREATE.ordinal());
            mapMessage.setString("CHATROOM", chatroom);
            mapMessage.setString("USER", user);
            sibylProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageChangePassword(String user, String pwd) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_USER_CHANGE_PASSWORD.ordinal());
            mapMessage.setString("USER", user);
            mapMessage.setString("PASSWORD", pwd);
            sibylProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageChangeRoom(String user, String chatroom) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_USER_JOIN_ROOM.ordinal());
            mapMessage.setString("USER", user);
            mapMessage.setString("CHATROOM", chatroom);
            sibylProducer.send(mapMessage);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    static boolean messageUnsubscribe(String chatroom) {
        try {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setInt("TYPE", MessageType.REQ_USER_LEAVE_ROOM.ordinal());
            mapMessage.setString("CHATROOM", chatroom);
            return true;
        } catch (JMSException e) {
            return false;
        }
    }
}