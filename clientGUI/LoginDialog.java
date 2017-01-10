package clientGUI;

import clientGUI.resources.languages.Language;
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
    private Language lan;

    public LoginDialog() {
        lan = new Language();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setLanguage();

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
        dispose();
    }

    private void onOK() {
        String usr = textUsename.getText();
        char[] pwd = passwordField.getPassword();
        passwordField.setText("");
        if (usr.length() == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrUsr"));
        else if (pwd.length == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrPwd"));
        else {
            ChatGUI.user = new User().setHandle(usr).setPassword(Arrays.toString(pwd));
            dispose();
        }
    }

    private void setLanguage() {
        this.labelInfo.setText(lan.getProperty("logLabInfo"));
        this.labelPassword.setText(lan.getProperty("pwd"));
        this.labelUsername.setText(lan.getProperty("usr"));
        this.buttonOK.setText(lan.getProperty("ok"));
    }
}
