package sibyl;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Topic;

/**
 * Created by jruiz on 11/11/16.
 */
public class ChatroomConnection {

    private Topic chatTopic;
    private String topicName; // ??
    private MessageProducer topicProducer;
    private MessageConsumer topicConsumer;

    public ChatroomConnection() {
    }

    public String getTopicName() {
        return topicName;
    } // ??

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    } // ??

    public Topic getTopic() {
        return chatTopic;
    }

    public MessageProducer getTopicProducer() {
        return topicProducer;
    }

    public MessageConsumer getTopicConsumer() {
        return topicConsumer;
    }

    public void setTopic(Topic chatTopic) {
        this.chatTopic = chatTopic;
    }

    public void setTopicProducer(MessageProducer topicProducer) {
        this.topicProducer = topicProducer;
    }

    public void setTopicConsumer(MessageConsumer topicConsumer) {
        this.topicConsumer = topicConsumer;
    }

}
