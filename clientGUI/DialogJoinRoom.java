package clientGUI;

import database.Chatroom;

import javax.swing.*;
import java.awt.event.*;

public class DialogJoinRoom extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel labelName;
    private JLabel labelIcon;
    private JTextField textName;
    private JTextField textIcon;
    private JPanel panelUp;
    private JPanel panelButtons;
    private JPanel panelDown;
    private Chatroom chatroom;
    private Language lan;

    DialogJoinRoom() {
        lan = new Language();
        this.setLanguage();
        setContentPane ( contentPane );
        setModal ( true );
        getRootPane ().setDefaultButton ( buttonOK );
        chatroom = null;

        buttonOK.addActionListener (e -> onOK ());

        buttonCancel.addActionListener (e -> dispose ());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation ( DO_NOTHING_ON_CLOSE );
        addWindowListener ( new WindowAdapter () {
            public void windowClosing(WindowEvent e) {
                dispose ();
            }
        } );

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction (e -> dispose (), KeyStroke.getKeyStroke
                ( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
    }

    private void onOK() {
        String topicName = textName.getText();
        String iconName = textIcon.getText ();
        if (topicName.length () > 16)
            JOptionPane.showMessageDialog(this, lan.getProperty("joiNameLeng"), "Error", JOptionPane.ERROR_MESSAGE);
        else if (topicName.length () == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("joiEmptyName"), "Error", JOptionPane.ERROR_MESSAGE);
        else if (iconName.length () != 0 && getClass ().getResource ( "clientGUI/resources/" + iconName ) == null)
            JOptionPane.showMessageDialog(this, lan.getProperty("joiNoIcon"), "Error", JOptionPane.ERROR_MESSAGE);
        else if (textName.getText ().length () != 0 ) {
            chatroom = new Chatroom();
            chatroom.setName(topicName);
            dispose ();
        }
    }

    private void setLanguage() {
        this.labelName.setText(lan.getProperty("joiRoom"));
        this.labelIcon.setText(lan.getProperty("joiRoomIc"));
        this.buttonOK.setText(lan.getProperty("ok"));
        this.buttonCancel.setText(lan.getProperty("cancel"));
    }

    public Chatroom getChatroom(){
        return chatroom;
    }
}
