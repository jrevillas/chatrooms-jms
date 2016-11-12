package sibyl;

import javax.jms.*;

/**
 * Created by jruiz on 11/7/16.
 */
public class UserConnection {

    private String handle;

    private Queue sibylReqQ;
    private Queue sibylResQ;

    private MessageConsumer sibylConsumerM;
    private MessageProducer sibylProducerM;

    public UserConnection() {
    }

    public String getHandle() {
        return handle;
    }

    public Queue getSibylReqQ() {
        return sibylReqQ;
    }

    public Queue getSibylResQ() {
        return sibylResQ;
    }

    public MessageConsumer getSibylConsumerM() {
        return sibylConsumerM;
    }

    public MessageProducer getSibylProducerM() {
        return sibylProducerM;
    }

    public void setSibylReqQ(Queue sibylReqQ) {
        this.sibylReqQ = sibylReqQ;
    }

    public void setSibylResQ(Queue sibylRes) {
        this.sibylResQ = sibylResQ;
    }

    public void setSibylConsumerM(MessageConsumer sibylConsumerM) {
        this.sibylConsumerM = sibylConsumerM;
    }

    public void setSibylProducerM(MessageProducer sibylProducerM) {
        this.sibylProducerM = sibylProducerM;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
