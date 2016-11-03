package clientGUI;

import database.Database;
import database.User;

import javax.swing.*;
import javax.swing.tree.ExpandVetoException;
import java.awt.event.*;
import java.util.Scanner;

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

    public LoginDialog() {
        setContentPane ( contentPane );
        setModal ( true );
        getRootPane ().setDefaultButton ( buttonOK );

        buttonOK.addActionListener ( new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                onOK ();
            }
        } );

        // call onCancel() when cross is clicked
        setDefaultCloseOperation ( DO_NOTHING_ON_CLOSE );
        addWindowListener ( new WindowAdapter () {
            public void windowClosing(WindowEvent e) {
                onCancel ();
            }
        } );
    }

    private void onCancel() {
        closed = true;
        dispose ();
    }

    private void onOK() {
        String usr = textUsename.getText ();
        char[] pwd = passwordField.getPassword ();
        passwordField.setText ( "" );
        if (usr.length () == 0)
            JOptionPane.showMessageDialog ( this, "You must specify an username" );
        else if (pwd.length == 0)
            JOptionPane.showMessageDialog ( this, "You must specify a pwd" );
        else
            switch (login(usr, pwd)) {
                case -1:
                    JOptionPane.showMessageDialog (this, "That user is already logged in and \n" +
                            "that's not it's password", "ERROR", JOptionPane.ERROR_MESSAGE) ;
                    break;
                case 0:
                    JOptionPane.showMessageDialog (this, "Welcome back " + usr, "LOGGED IN",
                            JOptionPane.INFORMATION_MESSAGE) ;
                    dispose ();
                    break;
                case 1:
                    JOptionPane.showMessageDialog (this, "Nice to meet you " + usr, "USER REGISTERED",
                            JOptionPane.INFORMATION_MESSAGE) ;
                    dispose ();
                    break;
            }
    }

    private int login(String usr, char[] pwd) {
        user = new User();
        user.setHandle(usr);
        user.setPassword(String.copyValueOf(pwd));
        return Database.login(user);
    }

    public User getUser() {
        return user;
    }

    public boolean getClosed() {
        return closed;
    }
}
