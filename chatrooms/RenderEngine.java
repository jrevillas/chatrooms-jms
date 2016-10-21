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
            scene[i] = " ";
        }
    }

    public static String[] getScene() {
        return scene;
    }

    public static void render() {
        StringBuilder str = new StringBuilder();
        for (String line : scene) {
            str.append(line + "\n");
        }
        System.out.print(str.toString());
    }

}
