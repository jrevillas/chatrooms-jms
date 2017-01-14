package clientGUI;

import clientGUI.resources.languages.Language;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

class PanelLogin extends JPanel {
    private Language lan;
    private JButton buttonLogIn;
    private JTextField textHandle;
    private JPasswordField textPassword;
    private ChatGUI chatGUI;

    PanelLogin() {
        lan = new Language();
        this.setLayout(new BorderLayout());
        Font light = new Font("Roboto Light", 0, 18);

        // CENTER
        JPanel panelCenter = new JPanel(new GridLayout(4, 1));
        panelCenter.setBorder(new EmptyBorder(5, 20, 15, 20));

        JLabel labelHandle = new JLabel(lan.getProperty("usr"));
        labelHandle.setFont(light);
        labelHandle.setForeground(Color.decode("#232323"));

        textHandle = new JTextField();
        textHandle.setPreferredSize(new Dimension(240, 30));
        textHandle.setFont(light);

        JLabel labelPassword = new JLabel(lan.getProperty("pwd"));
        labelPassword.setFont(light);
        labelPassword.setForeground(Color.decode("#232323"));

        textPassword = new JPasswordField();
        textPassword.setFont(light);

        panelCenter.add(labelHandle, 0);
        panelCenter.add(textHandle, 1);
        panelCenter.add(labelPassword, 2);
        panelCenter.add(textPassword, 3);

        // FOOTER
        JPanel panelFooter = new JPanel(new GridLayout(1, 2));

        Font normal = new Font("Roboto Light", 0, 18);

        buttonLogIn = new JButton(lan.getProperty("login"));
        buttonLogIn.setContentAreaFilled(false);
        buttonLogIn.setFocusPainted(false);
        buttonLogIn.setFont(normal);

        panelFooter.add(buttonLogIn);

        this.registerKeyboardAction(e -> loginAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // BUILDER
        this.add(panelCenter, BorderLayout.CENTER);
        this.add(panelFooter, BorderLayout.SOUTH);
    }

    PanelLogin setChatGUI(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
        buttonLogIn.addActionListener(e -> loginAction());
        return this;
    }

    private void loginAction() {
        buttonLogIn.setEnabled(false);
        String usr = textHandle.getText();
        String pwd = String.valueOf(textPassword.getPassword());
        if (usr.length() == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrUsrS"));
        else if (usr.length() > 10)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrUsrL"));
        else if (pwd.length() == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrPwdS"));
        else if (pwd.length() > 24)
            JOptionPane.showMessageDialog(this, lan.getProperty("logErrPwdL"));
        else {
            chatGUI.loginRequest(usr, pwd);
            return;
        }
        buttonLogIn.setEnabled(true);
    }

    void failedLogin (){
        buttonLogIn.setEnabled(true);
    }
}