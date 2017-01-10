package clientGUI;

import java.text.SimpleDateFormat;
import java.util.Date;

class MessageGUI {

    private String text;
    private String handle_user;
    private Date send_date;
    private String chatroom;
    private String mentions;

    MessageGUI() {
        send_date = new Date();
    }

    public String getText() {
        return text;
    }

    public MessageGUI setText(String text) {
        this.text = text;
        return this;
    }

    String getHandle_user() {
        return handle_user;
    }

    MessageGUI setHandle_user(String handle_user) {
        this.handle_user = handle_user;
        return this;
    }

    String getDate() {
        return new SimpleDateFormat("HH:mm").format(send_date);
    }

    MessageGUI setDate(long date) {
        send_date = new Date(date);
        return this;
    }

    String getChatroom() {
        return chatroom;
    }

    MessageGUI setChatroom(String chatroom) {
        this.chatroom = chatroom;
        return this;
    }

    String getMentions(){
        return mentions;
    }

    MessageGUI setMention(String mentions) {
        this.mentions = mentions;
        return this;
    }
}
