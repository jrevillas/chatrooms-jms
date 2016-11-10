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

    private static final String USER_HANDLE = "jrevillas";
    private static final String USER_PASSWORD = "12345";

    public static Session session;
    private static MessageConsumer sibylConsumer;
    private static MessageProducer sibylProducer;
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

            topic = session.createTopic("ClashRoyale");
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
                // System.out.println("Acaba de entrar un msg desde un topic");
                if (mapMsg.getInt("MSG_TYPE") == 0) {
                    RenderEngine.addMessage(mapMsg.getString("MSG_CONTENT"));
                }
            }
            // System.out.println("MSG_CONTENT -> " + mapMsg.getString("MSG_CONTENT"));

            System.out.print("\n");
            RenderEngine.render();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FancyConsumer fc = new FancyConsumer();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
