package chatrooms;

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

    public static void main(String[] args) {
        RenderEngine.addMessage("[@" + BLUE + "dmelero" + RESET + "] This is a test message.");
        RenderEngine.addMessage("[@" + CYAN + "jruiz" + RESET + "] This is another test message.");
        RenderEngine.addMessage("[@" + GREEN + "mnunez" + RESET + "] This is just another test message.");

        RenderEngine.getTopics().add("general");
        RenderEngine.getTopics().add("consejo-datsi");
        RenderEngine.getTopics().add("consejo-dia");
        RenderEngine.getTopics().add("consejo-dlsis");
        RenderEngine.getTopics().add("consejo-dmatic");

        RenderEngine.render();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(" > ");
            String input = scanner.nextLine();
            if (input.startsWith("newtopic")) {
                RenderEngine.getTopics().add(input.substring(input.lastIndexOf(" ") + 1));
            } else {
                RenderEngine.addMessage("[@" + PURPLE + "jrevillas" + RESET + "] " + input);
            }
            RenderEngine.render();
        }
    }

}
