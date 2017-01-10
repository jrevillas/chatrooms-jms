package clientGUI;

import database.Chatroom;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class ChatroomGUI extends Chatroom {
    private ImageIcon icon;
    private int unreadMessages;
    private boolean mention = false;
    private List<MessageGUI> messages;
    private boolean subscription;

    ChatroomGUI() {
        messages = new ArrayList<>();
    }

    private void setIcon(String icon) {
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

    void newMessage(MessageGUI message) {
        messages.add(message);
    }

    List<MessageGUI> getMessages() {
        return this.messages;
    }

    void eraseMessages() {
        this.messages.clear();
    }

    void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    void newNotification(boolean mention) {
        if (subscription)
            unreadMessages++;
        this.mention |= mention;
    }

    void addMessage(MessageGUI message) {
        messages.add(message);
    }
}