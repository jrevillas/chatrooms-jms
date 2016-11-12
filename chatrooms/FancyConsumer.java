package chatrooms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * Created by jrevillas on 07/11/2016.
 */
public class FancyConsumer implements javax.jms.MessageListener {

    // cualquier objeto se puede usar como un mutex al usarlo en un bloque synchronized
    public static Object mutex = new Object();

    private static final String USER_HANDLE = "jrevillas";
    private static final String USER_PASSWORD = "12345";

    public static Session session;
    private static MessageConsumer sibylConsumer;
    public static MessageProducer sibylProducer;
    private static Queue sibylRequests;
    private static Queue sibylResponses;

    private static MessageConsumer topicConsumer;
    public static MessageProducer topicProducer;
    private static Topic topic;

    public FancyConsumer() {
        try {
            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = myConnFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // CREATE_QUEUE(sibylreqRevillas)
            sibylRequests = session.createQueue("sibylreq" + "Revillas");

            // CREATE_QUEUE(sibylresRevillas)
            sibylResponses = session.createQueue("sibylres" + "Revillas");

            // CREATE_PRODUCER(sibylreqRevillas)
            sibylProducer = session.createProducer(sibylRequests);

            // CREATE_CONSUMER(sibylresRevillas)
            sibylConsumer = session.createConsumer(sibylResponses);

            //               ----------------
            // user1 -SEND-> sibylreqRevillas -RECV-> sibyl
            //               ----------------

            //               ----------------
            // sibyl -SEND-> sibylresRevillas -RECV-> user1
            //               ----------------

            topic = session.createTopic("topic0");
            topicConsumer = session.createConsumer(topic);
            topicProducer = session.createProducer(topic);
            topicConsumer.setMessageListener(this);

            sibylConsumer.setMessageListener(this);
            // mapMessage msg = session.createMapMessage();
            // msg.setString("MSG_CONTENT", "Me a request!");
            // msg.setInt("MSG_TYPE", 0);
            // sibylProducer.send(msg);
            // System.out.println("SENT - {'MSG_CONTENT':'" + msg.getString("MSG_CONTENT") + "','MSG_TYPE':" + msg.getInt("MSG_TYPE") + "}");
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message msg) {
        try {
            MapMessage mapMsg = (MapMessage) msg;
            if (mapMsg.getJMSDestination() instanceof Topic) {
                if (mapMsg.getInt("TYPE") == 0) {
                    RenderEngine.addMessage(mapMsg.getString("CONTENT"));
                }
            }

            if (mapMsg.getJMSDestination() instanceof Queue) {
                if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CREATE.ordinal()) {
                    RenderEngine.addTopic(mapMsg.getString("CHATROOM"));
                }

                if (mapMsg.getInt("TYPE") == MessageType.RES_ROOM_CHANGE_NAME.ordinal()) {
                    for (int i = 0; i < RenderEngine.getTopics().size(); i++) {
                        if (RenderEngine.getTopics().get(i).equals(mapMsg.getString("CHATROOM"))) {
                            RenderEngine.getTopics().set(i, mapMsg.getString("NEW"));
                            break;
                        }
                    }
                }
            }

            System.out.print("\n");
            synchronized (mutex) {
                RenderEngine.render();
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
                e.printStackTrace();
            }
        }
    }

}
