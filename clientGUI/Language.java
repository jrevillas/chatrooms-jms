package clientGUI;

import java.io.IOException;
import java.util.Properties;

public class Language extends Properties {
    private String[] languages = {"English", "Spanish"};
    static int index=0;

    public Language() {
        getProperties(languages[index]);
    }

    void setLanguage(int newIndex) {
        index = newIndex;
        getProperties(languages[index]);
    }

    private void getProperties(String keyword) {
        try {
            this.load(getClass().getResourceAsStream("resources/languages/" + keyword + ".properties"));
        } catch (IOException ignored) {
        }
    }
}
