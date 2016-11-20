package clientGUI;

import database.Chatroom;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.sun.jmx.snmp.EnumRowStatus.active;

class ChatroomGUI extends Chatroom {
    private ImageIcon icon;
    private int unreadMessages;
    private boolean mention;
    private List<MessageGUI> messages;

    public ChatroomGUI() {
        messages = new ArrayList<>();
    }

    void setIcon(String icon) {
        this.icon = new ImageIcon(getClass().getResource("/clientGUI/resources/" + icon));
    }

    ImageIcon getIcon() {
        if (icon == null)
            setIcon("example.png");
        return this.icon;
    }

    int getUnreadMessages() {
        return unreadMessages;
    }

    void setZeroMessages() {
        unreadMessages = 0;
        mention = false;
    }

    void setMention() {
        this.mention = true;
    }

    public ChatroomGUI setName(String name) {
        super.setName(name);
        return this;
    }

    boolean getMention() {
        return this.mention;
    }

    void newMessage(MessageGUI message, boolean active) {
        messages.add(message);
        if (!active)
            this.unreadMessages++;
    }

    List<MessageGUI> getMessages() {
        return this.messages;
    }
}