package chatrooms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.twitter.Extractor;

/**
 * Created by jrevillas on 21/10/2016.
 */
public class RenderEngineTest {

    private static final String BLACK = "\u001B[30m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";

    private static final String PIZZA = "\uD83C\uDF46";
    private static final String HEART = "\uD83D\uDC9D ";
    private static final String CAKE = "\uD83C\uDF70";
    private static final String MEAT = "\uD83C\uDF57";
    private static final String POO = "\uD83D\uDCA9";

    public static boolean goodLogin = false;
    private static Extractor extractor = new Extractor();

    public RenderEngineTest() {}

    private static String msgPrefix;

    public static String userHandle;
    public static String userChatroom = "lobby"; // TODO dynamic update


    public static BufferedReader br;

    public static void main(String[] args) throws JMSException {

        // args[0] - nombre de usuario
        // args[1] - contraseña

        if (args.length < 2) {
            System.out.println("USAGE: ./chatrooms-jms.jar username password");
            return;
        }

        // sacar el usuario y contraseña
        userHandle = args[0];
        msgPrefix = "[@" + BLUE + userHandle + RESET + "] ";


        FancyConsumer consumer = new FancyConsumer();

        RenderEngine.render();

        MapMessage message = FancyConsumer.session.createMapMessage();
        message.setInt("TYPE", MessageType.REQ_LOGIN.ordinal());
        message.setString("PASSWORD", args[1]);
        message.setString("USER", args[0]);
        FancyConsumer.sibylProducer.send(message);

        while (true) {
            scan();
        }
    }

    public static String checkMentions(String msgContent) {
        if (!msgContent.contains("@")) {
            return null;
        }
        List<String> mentions = extractor.extractMentionedScreennames(msgContent);
        return mentions.isEmpty() ? null : String.join(",", mentions);
    }

    public static void scan() {
        try {
            while (true) {
                br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();
                String mentions;

                if (input.startsWith("/")) {
                    // Si el mensaje empieza por "/", tratar con la clase SlashInterpreter, ya que se tendrá que
                    // enviar por la cola de peticiones cliente-sibyl.
                    SlashInterpreter.handle(input);
                } else if ((mentions = checkMentions(input)) != null) {
                    // si el mensaje incluye menciones, extrae las menciones, crea un mensaje con menciones
                    // (MSG_WITH_MENTIONS) y envíalo por el topic correspondiente.
                    MapMessage message = FancyConsumer.session.createMapMessage();
                    message.setInt("TYPE", MessageType.MSG_WITH_MENTIONS.ordinal());
                    message.setString("CONTENT", msgPrefix + input);
                    message.setString("MENTIONS", mentions);
                    message.setString("USER", userHandle);
                    message.setString("CHATROOM", userChatroom);
                    FancyConsumer.topicProducer.send(message);
                    RenderEngine.addMessage("\u001B[30m(mentions) " + mentions + RESET);
                } else {
                    // Si el mensaje no empieza por "/" ni tampoco incluye menciones, encapsula el contenido en un
                    // mensaje normal (MSG_SIMPLE) y envíalo por el topic correspondiente.
                    MapMessage message = FancyConsumer.session.createMapMessage();
                    message.setInt("TYPE", MessageType.MSG_SIMPLE.ordinal());
                    message.setString("CONTENT", msgPrefix + input);
                    message.setString("USER", userHandle);
                    message.setString("CHATROOM", userChatroom);
                    FancyConsumer.topicProducer.send(message);
                    System.out.println("Acabas de enviar un mensaje por " + userChatroom);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
