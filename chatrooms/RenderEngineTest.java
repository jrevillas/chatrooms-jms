package chatrooms;

/**
 * Created by jrevillas on 21/10/2016.
 */
public class RenderEngineTest {

    public static void main(String[] args) {
        RenderEngine.getTopics().add("general");
        RenderEngine.getTopics().add("consejo-datsi");
        RenderEngine.getTopics().add("consejo-dia");
        RenderEngine.getTopics().add("consejo-dlsis");
        RenderEngine.getTopics().add("consejo-dmatic");
        RenderEngine.render();
    }

}
