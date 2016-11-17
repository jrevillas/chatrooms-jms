package clientGUI;
import javax.jms.*;

class DynamicProducerGUI {

    private MessageProducer msgProducer;
    private MessageProducer sibylProducer;

    DynamicProducerGUI() throws JMSException {
    }

    DynamicProducerGUI setProducer(Session session) {
        try {
            Topic topic = session.createTopic("topic1");
            Queue sibylQueue = session.createQueue("sibylreqRevillas");
            msgProducer = session.createProducer(topic);
            sibylProducer = session.createProducer(sibylQueue);
            return this;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    void sendMessage(MapMessage message) {
        try {
            System.out.println("INFO: enviando mensaje " + message.getString("CHATROOM"));
            if (message.getInt("TYPE") > 0) {
                sibylProducer.send(message);
            } else {
                msgProducer.send(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}
