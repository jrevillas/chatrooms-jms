package clientGUI;

import database.User;
import sibyl.DynamicProducer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogLogin extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField textUsename;
    private JPasswordField passwordField;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JLabel labelInfo;
    private Language lan;
    private DialogLoading dialogLoading;

    DialogLogin() {
        lan = new Language();
        this.setName("Login");
        setContentPane(contentPane);
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
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrUsrS"));
        else if (usr.length() > 10)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrUsrL"));
        else if (pwd.length == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrPwdS"));
        else if (pwd.length > 24)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrPwdL"));
        else {
            ChatGUI.user = new User().setHandle(usr).setPassword(String.valueOf(pwd));
            DynamicProducerGUI.setProducer(usr);
            ChatGUI.consumer.setHandler(usr);
            this.setEnabled(false);
            DynamicProducerGUI.messageLogin(usr, String.valueOf(pwd));
            dialogLoading = new DialogLoading();
            dialogLoading.setVisible(true);
            dialogLoading.setLocationRelativeTo(this);
        }
    }

    void loginResponse(boolean response) {
        if (dialogLoading != null) {
            dialogLoading.dispose();
            if (response) {
                JOptionPane.showMessageDialog(this, lan.getProperty("loLogg"), "OK", JOptionPane.PLAIN_MESSAGE);
                dispose();
            } else {
                this.setEnabled(true);
                JOptionPane.showMessageDialog(this, lan.getProperty("loWrPwd"), "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setLanguage() {
        this.labelInfo.setText(lan.getProperty("logLabInfo"));
        this.labelPassword.setText(lan.getProperty("pwd"));
        this.labelUsername.setText(lan.getProperty("usr"));
        this.buttonOK.setText(lan.getProperty("ok"));
    }

    public static void main(String[] args) {

    }
}
