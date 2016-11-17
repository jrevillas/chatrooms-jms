package clientGUI;

import chatrooms.FancyConsumer;
import chatrooms.MessageType;
import chatrooms.RenderEngine;

import javax.jms.*;

public class FancyConsumerGUI implements javax.jms.MessageListener {

    // cualquier objeto se puede usar como un mutex al usarlo en un bloque synchronized
    public static Object mutex = new Object();

    private static final String USER_HANDLE = "jrevillas";
    private static final String USER_PASSWORD = "12345";

    private static MessageConsumer sibylConsumer;
    public static MessageProducer sibylProducer;
    private static Queue sibylRequests;
    private static Queue sibylResponses;

    private static MessageConsumer topicConsumer;
    public static MessageProducer topicProducer;
    private static Topic topic;

    private ChatGUI chatGUI;

    public FancyConsumerGUI()  {}

    public void setChatGUI(ChatGUI gui) throws JMSException {
        this.chatGUI = gui;
        ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
        Connection connection = myConnFactory.createConnection();
        chatGUI.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // CREATE_QUEUE(sibylreqRevillas)
        sibylRequests = chatGUI.session.createQueue("sibylreq" + "Revillas");

        // CREATE_QUEUE(sibylresRevillas)
        sibylResponses = chatGUI.session.createQueue("sibylres" + "Revillas");

        // CREATE_PRODUCER(sibylreqRevillas)
        sibylProducer = chatGUI.session.createProducer(sibylRequests);

        // CREATE_CONSUMER(sibylresRevillas)
        sibylConsumer = chatGUI.session.createConsumer(sibylResponses);

        //               ----------------
        // user1 -SEND-> sibylreqRevillas -RECV-> sibyl
        //               ----------------

        //               ----------------
        // sibyl -SEND-> sibylresRevillas -RECV-> user1
        //               ----------------

        topic = chatGUI.session.createTopic("topic1");
        topicConsumer = chatGUI.session.createConsumer(topic);
        topicProducer = chatGUI.session.createProducer(topic);
        topicConsumer.setMessageListener(this);
        sibylConsumer.setMessageListener(this);
        // mapMessage msg = session.createMapMessage();
        // msg.setString("MSG_CONTENT", "Me a request!");
        // msg.setInt("MSG_TYPE", 0);
        // sibylProducer.send(msg);
        // System.out.println("SENT - {'MSG_CONTENT':'" + msg.getString("MSG_CONTENT") + "','MSG_TYPE':" + msg.getInt("MSG_TYPE") + "}");
        connection.start();
    }

    public void onMessage(Message msg) {
        try {
            System.out.println("INFO: mensaje recibido");
            MapMessage mapMsg = (MapMessage) msg;
            if (mapMsg.getJMSDestination() instanceof Topic) {
                if (mapMsg.getInt("TYPE") == 0) {
                    MessageGUI message = new MessageGUI()
                            .setText(mapMsg.getString("CONTENT"))
                            .setDate(mapMsg.getJMSTimestamp())
                            .setHandle_user(mapMsg.getString("USER"));
                    chatGUI.printMessage(message);
                }
            }

            if (mapMsg.getJMSDestination() instanceof Queue) {
                if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CREATE.ordinal()) {
                    chatGUI.createRoom(mapMsg.getString("CHATROOM"));
                } else if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CHANGE_NAME.ordinal()) {
                    chatGUI.changeRoomName(mapMsg.getString("CHATROOM"), mapMsg.getString("NEW"));
                } else if (mapMsg.getInt("TYPE") == MessageType.RES_LOGIN.ordinal()) {
                    chatGUI.loginOK(mapMsg.getBoolean("STATUS"), mapMsg.getString("TOPICS"));
                }
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
                break;
            }
        }
    }
}
