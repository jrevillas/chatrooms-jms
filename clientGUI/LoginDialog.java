package clientGUI;

import database.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField textUsename;
    private JPasswordField passwordField;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JLabel labelInfo;
    private User user;
    private boolean closed = false;
    private int login = -2;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    private void onCancel() {
        closed = true;
        dispose();
    }

    private void onOK() {
        String usr = textUsename.getText();
        char[] pwd = passwordField.getPassword();
        passwordField.setText("");
        if (usr.length() == 0)
            JOptionPane.showMessageDialog(this, "You must specify an username");
        else if (pwd.length == 0)
            JOptionPane.showMessageDialog(this, "You must specify a pwd");
        else {
            user = new User().setHandle(usr).setPassword(Arrays.toString(pwd));
            dispose();
        }
    }

    public User getUser() {
        return user;
    }

    public boolean getClosed() {
        return closed;
    }
}
