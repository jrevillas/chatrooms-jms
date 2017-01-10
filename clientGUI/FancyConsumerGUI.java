package clientGUI;

import chatrooms.MessageType;
import javax.jms.*;

public class FancyConsumerGUI implements MessageListener {
    private ChatGUI chatGUI;
    private MessageConsumer topicConsumer;
    private Session session;
    private boolean logged = false;

    /**
     * <B>FUNCTION: creates the session</B>
     * @param gui (the main application)
     * @return the consumer
     * @throws JMSException if there is no connection with the brooker
     */
    FancyConsumerGUI setChatGUI(ChatGUI gui) throws JMSException {
        chatGUI = gui;
        ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
        Connection connection = myConnFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        chatGUI.setSession(session);
        connection.start();
        return this;
    }

    /**
     * <B>FUNCTION:</B> creates the queue for Sibyl requests
     * @param handler login name
     */
    void setHandler(String handler) {
        try {
            Queue sibylResponses = session.createQueue("sibylres" + handler);
            MessageConsumer sibylConsumer = session.createConsumer(sibylResponses);
            sibylConsumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * <B>FUNCTION:</B> changes the subscription to the new topic
     * @param topicName name of the new topic "topicX"
     */
    void changeRoom(String topicName) {
        try {
            if (topicConsumer != null)
                topicConsumer.close();
            DynamicProducerGUI.changeRoom(topicName);
            Topic topic = session.createTopic(topicName);
            topicConsumer = session.createConsumer(topic);
            topicConsumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * <B>FUNCTION:</B> method to receive messages
     * @param msg message received
     */
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
                            if (mapMsg.getBoolean("MENTION")) // TODO
                                System.out.println("TE HAN MENCIONADO!!!!!");
                            System.out.println("");
                            break;
                        case RES_ROOM_CHANGE_NAME:
                            chatGUI.changeRoomName(true, mapMsg.getString("CHATROOM"),
                                    mapMsg.getString("NEW"));
                            break;
                        case RES_ROOM_CREATE:
                            chatGUI.createRoom(true, mapMsg.getBoolean("STATUS"));
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
