package chatrooms;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Created by jrevillas on 11/11/2016.
 */
public class SlashInterpreter {

    private static final String FLOWER = "\uD83C\uDF38 ";
    private static final String HEART = "\uD83D\uDC9D ";
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private static final String msgPrefix = "[@" + RED + "jrevillas" + RESET + "] ";

    private static String[] love = new String[7];

    static {
        love[0] = "  " + HEART + HEART + HEART + "  " + HEART + HEART + HEART;
        love[1] = HEART + HEART + "  " + HEART + HEART + HEART + "  " + HEART + HEART;
        love[2] = HEART + "      " + HEART + "      " + HEART;
        love[3] = HEART + HEART + "          " + HEART + HEART;
        love[4] = "  " + HEART + HEART + "      " + HEART + HEART;
        love[5] = "    " + HEART + HEART + "  " + HEART + HEART;
        love[6] = "        " + HEART;
    }

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
            message.setString("USER", RenderEngineTest.userHandle);
            message.setString("CHATROOM", RenderEngineTest.userChatroom);
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
        if (args[0].equals("/goto")) {
            RenderEngine.addMessage("\u001B[30m > " + command + RESET);
            MapMessage msg = FancyConsumer.session.createMapMessage();
            msg.setInt("TYPE", MessageType.REQ_USER_JOIN_ROOM.ordinal());
            msg.setString("CHATROOM", args[1]);
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
        if (args[0].equals("/love")) {
            MapMessage msg = FancyConsumer.session.createMapMessage();
            for (String str : love) {
                msg.setInt("TYPE", MessageType.MSG_SIMPLE.ordinal());
                msg.setString("CONTENT", str);
                msg.setString("CHATROOM", RenderEngineTest.userChatroom);
                msg.setString("USER", RenderEngineTest.userHandle);
                FancyConsumer.topicProducer.send(msg);
            }
        }
    }

}
