package chatrooms;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Created by jrevillas on 11/11/2016.
 */
public class SlashInterpreter {

    private static final String FLOWER = "\uD83C\uDF38 ";
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";

    public static void handle(String command) throws JMSException {
        // > /slashcommand arg1 arg2
        // args[0] = "/slashcommand"
        // args[1] = "arg1"
        // args[2] = "arg2"
        String[] args = command.split("\\s");
        if (args[0].equals("/slash")) {
            RenderEngine.addMessage("\u001B[30m > " + command + RESET);
            MapMessage message = FancyConsumer.session.createMapMessage();
            message.setInt("TYPE", 0);
            message.setString("CONTENT", "[" + PURPLE + "slash" + RESET + "] I hear you sister!");
            message.setString("USER", "slash");
            message.setString("CHATROOM", "lobby");
            FancyConsumer.topicProducer.send(message);
        }
        if (args[0].equals("/rename")) {
            RenderEngine.addMessage("\u001B[30m > " + command + RESET);
            MapMessage msg = FancyConsumer.session.createMapMessage();
            msg.setInt("TYPE", MessageType.REQ_ROOM_CHANGE_NAME.ordinal());
            msg.setString("CHATROOM", args[1]);
            msg.setString("NEW", args[2]);
            msg.setString("USER", RenderEngineTest.userHandle);
            FancyConsumer.sibylProducer.send(msg);
        }
        if (args[0].equals("/create")) {
            RenderEngine.addMessage("\u001B[30m > " + command + RESET);
            MapMessage msg = FancyConsumer.session.createMapMessage();
            msg.setInt("TYPE", MessageType.REQ_ROOM_CREATE.ordinal());
            msg.setString("CHATROOM", args[1]);
            msg.setString("USER", RenderEngineTest.userHandle);
            FancyConsumer.sibylProducer.send(msg);
        }
        if (args[0].equals("/password")) {
            RenderEngine.addMessage("\u001B[30m > " + "/password *****" + RESET);
            MapMessage msg = FancyConsumer.session.createMapMessage();
            msg.setInt("TYPE", MessageType.REQ_USER_CHANGE_PASSWORD.ordinal());
            msg.setString("USER", RenderEngineTest.userHandle);
            msg.setString("PASSWD", args[1]);
            FancyConsumer.sibylProducer.send(msg);
        }
    }

}
