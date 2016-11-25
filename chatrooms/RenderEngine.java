package chatrooms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jrevillas on 21/10/2016.
 */
public class RenderEngine {

    private static final String BLACK = "\u001B[30m";
    private static final String BLUE = "\u001B[34m";
    private static int currentTopic;
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static List<String> messages;
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static String[] scene;
    private static final int SCENE_HEIGHT = 30;
    private static final int SCENE_WIDTH = 120;
    private static List<TopicWithMessages> topics;
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";

    public static class TopicWithMessages {
        public TopicWithMessages(String name) {
            this.name = name;
            this.messages = 0;
        }

        public String name;
        public int messages;
    }

    static {
        currentTopic = 0;
        messages = new LinkedList<String>();
        scene = new String[SCENE_HEIGHT - 1];
        topics = new ArrayList<TopicWithMessages>();

        for (int i = 0; i < scene.length; i++) {
            scene[i] = "";
        }
    }

    public static int getCurrentTopic() {
        return currentTopic;
    }

    public static List getMessages() {
        return messages;
    }

    public static String[] getScene() {
        return scene;
    }

    public static List<TopicWithMessages> getTopics() {
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

    public static void addMessage(String msg) {
        if (messages.size() > SCENE_HEIGHT - 4) {
            messages.remove(0);
        }
        messages.add(msg);
    }

    public static void addTopic(String topic) {
        if (topics.size() > SCENE_HEIGHT - 4) {
            topics.remove(0);
        }
        for (TopicWithMessages t : topics) {
            if (t.name.equals(topic)) {
                return;
            }
        }
        topics.add(new TopicWithMessages(topic));
    }

    public static void notify(String topic) {
        System.out.println("Se ha llamado a notify con el topic " + topic);
        for (TopicWithMessages internalTopic : topics) {
            if (internalTopic.name.equals(topic)) {
                internalTopic.messages = internalTopic.messages + 1;
                System.out.println("Ahora messages vale " + internalTopic.messages);
                break;
            }
        }
    }

    public static void render() {
        renderTopics();
        renderMessages();

        StringBuilder str = new StringBuilder();
        for (String line : scene) {
            str.append(line + "\n");
        }

        str.append(" > ");

        System.out.print(str.toString());
        // Wingardium Leviosa
        // System.out.flush();
    }

    private static void renderMessages() {
        for (int i = 0; i < messages.size(); i++) {
            scene[i + 1] += "   " + messages.get(i);
        }
    }

    private static String push(int i) {
        if (i == 0) {
            return " ";
        }
        if (i < 10) {
            return "\u001B[31m" + i + "\u001B[0m";
        }
        if (i >= 10) {
            return "\u001B[31m+\u001B[0m";
        }
        return " ";
    }


    private static void renderTopics() {
        if (topics.size() != 0) {
            scene[1] = " ┌ #" + rightPad(topics.get(0).name, 15) + " [" + push(topics.get(0).messages) + "] ┐";
            for (int i = 1; i < topics.size() - 1; i++) {
                scene[i + 1] = " │ #" + rightPad(topics.get(i).name, 15) + " [" + push(topics.get(i).messages) + "] │";
            }
            scene[topics.size()] = " └ #" + rightPad(topics.get(topics.size() - 1).name, 15) + " [" + push(topics.get(topics.size() - 1).messages) + "] ┘";
        }

        for (int i = 1; i < scene.length - topics.size(); i++) {
            scene[topics.size() + i] = rightPad("", 25);
        }
    }

    private static String rightPad(String str, int padding) {
        return String.format("%1$-" + padding + "s", str);
    }

}
