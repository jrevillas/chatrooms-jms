package clientGUI;

import database.Chatroom;

import javax.swing.*;
import java.awt.event.*;

public class JoinRoomDialog extends JDialog {
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

    public JoinRoomDialog() {
        setContentPane ( contentPane );
        setModal ( true );
        getRootPane ().setDefaultButton ( buttonOK );
        chatroom = null;

        buttonOK.addActionListener ( new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                onOK ();
            }
        } );

        buttonCancel.addActionListener ( new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                onCancel ();
            }
        } );

        // call onCancel() when cross is clicked
        setDefaultCloseOperation ( DO_NOTHING_ON_CLOSE );
        addWindowListener ( new WindowAdapter () {
            public void windowClosing(WindowEvent e) {
                onCancel ();
            }
        } );

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction ( new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                onCancel ();
            }
        }, KeyStroke.getKeyStroke ( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
    }

    private void onOK() {
        String topicName = textName.getText();
        String iconName = textIcon.getText ();
        if (topicName.length () > 16)
            JOptionPane.showMessageDialog(this, "Try with a smaller name (lees than 16 chars)", "Error", JOptionPane.ERROR_MESSAGE);
        else if (topicName.length () == 0)
            JOptionPane.showMessageDialog(this, "C'mon where is your imagination", "Error", JOptionPane.ERROR_MESSAGE);
        else if (iconName.length () != 0 && getClass ().getResource ( "resources/" + iconName ) == null)
            JOptionPane.showMessageDialog(this, "Nice try, but I can't find that icon", "Error", JOptionPane.ERROR_MESSAGE);
        else if (textName.getText ().length () != 0 ) {
            chatroom = new Chatroom();
            chatroom.setName(topicName);
            chatroom.setIcon(iconName);
            dispose ();
        }
    }

    private void onCancel() {
        dispose ();
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    public Chatroom getChatroom(){
        return chatroom;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
