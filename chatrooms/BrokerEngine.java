package chatrooms;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Topic;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import java.util.Scanner;

/**
 * Created by jrevillas on 22/10/2016.
 */
public class BrokerEngine {

    // no se vuelve a usar, podemos dejarlo dentro del main()
    public static ConnectionFactory connectionFactory;

    // a lo mejor la deberíamos cerrar en algún momento desde otra parte del código. Se queda.
    public static Connection connection;

    public static Session session;

    // ArrayList de topics o añadirlos cuando creados para pintar
    public static Topic topic;

    public static MessageProducer msgProducer;

    public static void main(String[] args) {
        try {
            connectionFactory = new com.sun.messaging.ConnectionFactory();
            // Connection connection = connectionFactory.createConnection();
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Topic topic = session.createTopic("general");
            topic = createTopic("general");
            createTopic("consejodlsis");
            createTopic("consejodatsi");
            createTopic("consejodia");
            createTopic("consejodmatic");

            MessageProducer msgProducer = session.createProducer(topic);
            TextMessage textMsg = session.createTextMessage();
            textMsg.setText("test");
            // System.out.println("Sending message: " + textMsg.getText());
            msgProducer.send(textMsg);

            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print(" > ");
                String input = scanner.nextLine();
                if (input.equals(":pizza:")) {
                    createTopic(input.substring(input.lastIndexOf(" ") + 1));
                }
                if (input.startsWith("newtopic")) {
                    createTopic(input.substring(input.lastIndexOf(" ") + 1));
                }
                if (input.equals("msg")) {
                    TextMessage msg = session.createTextMessage();
                    msg.setText("ima test msg :D");
                    msgProducer.send(msg);
                }
                RenderEngine.render();
            }

            // session.close();
            // connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static Topic createTopic(String name) {
        System.out.println("Creating topic: #" + name);
        try {
            Topic topic = new com.sun.messaging.Topic(name);
            RenderEngine.getTopics().add(name);
            if (RenderEngine.getTopics().size() > 2) {
                RenderEngine.render();
            }
            return topic;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
