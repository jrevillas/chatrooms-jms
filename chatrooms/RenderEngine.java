package chatrooms;

import java.util.ArrayList;

/**
 * Created by jrevillas on 21/10/2016.
 */
public class RenderEngine {

    private static final String BLACK = "\u001B[30m";
    private static final String BLUE = "\u001B[34m";
    private static int currentTopic;
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static ArrayList messages;
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static String[] scene;
    private static final int SCENE_HEIGHT = 30;
    private static final int SCENE_WIDTH = 120;
    private static ArrayList topics;
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";

    static {
        currentTopic = 0;
        messages = new ArrayList<String>();
        scene = new String[SCENE_HEIGHT - 1];
        topics = new ArrayList<String>();

        for (int i = 0; i < scene.length; i++) {
            scene[i] = "";
        }
    }

    public static int getCurrentTopic() {
        return currentTopic;
    }

    public static ArrayList getMessages() {
        return messages;
    }

    public static String[] getScene() {
        return scene;
    }

    public static ArrayList getTopics() {
        return topics;
    }

    public static void setCurrentTopic(int currentTopic) {
        RenderEngine.currentTopic = currentTopic;
    }

    public static void setMessages(ArrayList messages) {
        RenderEngine.messages = messages;
    }

    public static void setScene(String[] scene) {
        RenderEngine.scene = scene;
    }

    public static void setTopics(ArrayList topics) {
        RenderEngine.topics = topics;
    }

    private static String leftPad(String str, int padding) {
        return String.format("%1$" + padding + "s", str);
    }

    public static void render() {
        renderTopics();

        StringBuilder str = new StringBuilder();
        for (String line : scene) {
            str.append(line + "\n");
        }
        System.out.print(str.toString());
    }

    private static void renderTopics() {
        scene[1] = " ┌ #" + rightPad(topics.get(0).toString(), 15) + " [ ] ┐";
        for (int i = 1; i < topics.size() - 1; i++) {
            scene[i + 1] = " │ #" + rightPad(topics.get(i).toString(), 15) + " [ ] │";
        }
        scene[topics.size()] = " └ #" + rightPad(topics.get(topics.size() - 1).toString(), 15) + " [ ] ┘";
    }

    private static String rightPad(String str, int padding) {
        return String.format("%1$-" + padding + "s", str);
    }

}
