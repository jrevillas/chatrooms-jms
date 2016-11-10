package chatrooms;

import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

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
    private static final String CAKE = "\uD83C\uDF70";
    private static final String MEAT = "\uD83C\uDF57";
    private static final String POO = "\uD83D\uDCA9";

    private static final String msgPrefix = "[@" + RED + "jrevillas" + RESET + "] ";

    public static BufferedReader br;

    public static void main(String[] args) {
        FancyConsumer consumer = new FancyConsumer();

        RenderEngine.addMessage("[@" + PURPLE + "dmelero" + RESET + "] This is a test message.");
        RenderEngine.addMessage("[@" + CYAN + "jruiz" + RESET + "] This is another test message.");
        RenderEngine.addMessage("[@" + GREEN + "mnunez" + RESET + "] This is just another test message.");

        RenderEngine.getTopics().add("general");
        RenderEngine.getTopics().add("consejo-datsi");
        RenderEngine.getTopics().add("consejo-dia");
        RenderEngine.getTopics().add("consejo-dlsis");
        RenderEngine.getTopics().add("consejo-dmatic");

        RenderEngine.render();

        while (true) {
            scan();
        }
    }

    public static void scan() {
        try {
            while (true) {
                br = new BufferedReader(new InputStreamReader(System.in));
                // System.out.print(" > ");
                String input = br.readLine();
                // br.close();
                // RenderEngine.addMessage(msgPrefix + input);

                RenderEngine.render();

                MapMessage message = FancyConsumer.session.createMapMessage();
                message.setInt("MSG_TYPE", 0);
                message.setString("MSG_CONTENT", msgPrefix + input);
                message.setString("MSG_USER", "Revillas");
                message.setString("MSG_CHATROOM", "ClashRoyale");
                FancyConsumer.topicProducer.send(message);

                // RenderEngine.render();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeScanner() {
        // TODO scanner.close() no deberia utilizarse, cerraria stdin
    }

}
