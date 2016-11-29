package clientGUI;

import chatrooms.MessageType;

import javax.jms.*;

public class FancyConsumerGUI implements MessageListener {
    private ChatGUI chatGUI;
    private MessageConsumer topicConsumer;
    private boolean logged = false;

    FancyConsumerGUI setChatGUI(ChatGUI gui) throws JMSException {
        chatGUI = gui;
        ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
        Connection connection = myConnFactory.createConnection();
        ChatGUI.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
        return this;
    }

    void setHandler(String handler) {
        try {
            Queue sibylResponses = ChatGUI.session.createQueue("sibylres" + handler);
            MessageConsumer sibylConsumer = ChatGUI.session.createConsumer(sibylResponses);
            sibylConsumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    void changeRoom(String topicName) {
        try {
            if (this.topicConsumer != null)
                topicConsumer.close();
            DynamicProducerGUI.changeRoom(topicName);
            Topic topic = ChatGUI.session.createTopic(topicName);
            topicConsumer = ChatGUI.session.createConsumer(topic);
            topicConsumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message msg) {
        try {
            MapMessage mapMsg = (MapMessage) msg;
            if (logged) {
                System.out.println("INFO: mensaje recibido " + MessageType.values()[mapMsg.getInt("TYPE")]);
                if (mapMsg.getJMSDestination() instanceof Topic) {
                    MessageGUI message;
                    if (mapMsg.getInt("TYPE") == MessageType.MSG_SIMPLE.ordinal()) {
                        message = new MessageGUI()
                                .setText(mapMsg.getString("CONTENT"))
                                .setDate(mapMsg.getJMSTimestamp())
                                .setHandle_user(mapMsg.getString("USER"))
                                .setChatroom(mapMsg.getString("CHATROOM"))
                                .setMention(mapMsg.getString("MENTIONS"));
                        chatGUI.printMessage(message);
                    }
                }
                if (mapMsg.getJMSDestination() instanceof Queue) {
                    int type = mapMsg.getInt("TYPE");
                    switch (MessageType.values()[type]) {
                        case RES_NEW_MESSAGE:
                            chatGUI.newMessage(mapMsg.getString("CHATROOM"), mapMsg.getBoolean("MENTION"));
                            break;
                        case RES_ROOM_CHANGE_NAME:
                            chatGUI.changeRoomName(true, mapMsg.getString("CHATROOM"), mapMsg.getString("NEW"));
                            break;
                        case RES_ROOM_CREATE:
                            chatGUI.createRoom(true, 0);
                            break;
                        case RES_USER_CHANGE_PASSWORD:
                            chatGUI.openSettings(true, mapMsg.getBoolean("STATUS"));
                            break;
                        case RES_USER_JOIN_ROOM:
                            chatGUI.changeRoomRes(mapMsg.getString("CONTENT"), mapMsg.getString("TOPIC"));
                            break;
                        case RES_USER_LEAVE_ROOM:
                            chatGUI.unsubscribe(true);
                            break;
                        case RES_LOGIN:
                            break;
                    }
                }
            }
            else {
                if(mapMsg.getInt("TYPE") == MessageType.RES_LOGIN.ordinal()) {
                    logged = mapMsg.getBoolean("STATUS");
                    chatGUI.start(logged, mapMsg.getString("TOPICS"));
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
