package chatrooms;

/**
 * Created by jrevillas on 21/10/2016.
 */
public class RenderEngineTest {

    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        RenderEngine.getScene()[1] += "┌ #general         [+] ┐";
        RenderEngine.getScene()[2] += "│ #consejo-datsi   [" + RED + "·" + RESET + "] │";
        RenderEngine.getScene()[3] += "│ " + BLUE + "#consejo-dia" + RESET + "         │";
        RenderEngine.getScene()[4] += "│ #consejo-dlsis   [5] │";
        RenderEngine.getScene()[5] += "└ #consejo-dmatic  [0] ┘";
        RenderEngine.render();
    }

}
