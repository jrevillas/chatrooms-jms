package clientGUI;

import database.Chatroom;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class ChatroomGUI extends Chatroom {
    private ImageIcon icon;
    private int unreadMessages = 0;
    private boolean mention = false;
    private List<MessageGUI> messages;
    private static int random = 1;

    ChatroomGUI() {
        messages = new ArrayList<>();
    }

    private void setIcon(String icon) {
        this.icon = new ImageIcon(getClass().getResource("/clientGUI/resources/" + icon));
        this.icon = new ImageIcon(this.icon.getImage().getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH));
    }

    ImageIcon getIcon() {
        if (icon == null) {
            setIcon("avatar" +  (1+(random++%4)) + ".png");
        }
        return this.icon;
    }

    int getUnreadMessages() {
        return unreadMessages;
    }

    void setZeroMessages() {
        unreadMessages = 0;
        mention = false;
    }

    void newMessage(boolean mention) {
        if (mention)
            this.mention = true;
        unreadMessages++;

    }

    public ChatroomGUI setName(String name) {
        super.setName(name);
        return this;
    }

    boolean getMention() {
        return this.mention;
    }

    List<MessageGUI> getMessages() {
        return this.messages;
    }

    void eraseMessages() {
        this.messages.clear();
    }
}