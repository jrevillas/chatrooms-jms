package clientGUI;

import clientGUI.resources.languages.Language;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class PanelLogin extends JPanel {
    private Language lan;
    private JButton buttonLogIn;
    private JTextField textHandle;
    private JPasswordField textPassword;
    private ChatGUI chatGUI;

    PanelLogin() {
        lan = new Language();
        this.setLayout(new BorderLayout());

        // CENTER
        JPanel panelCenter = new JPanel(new GridLayout(4, 1));
        panelCenter.setBorder(new EmptyBorder(5, 20, 15, 20));

        JLabel labelHandle = new JLabel("Handle");
        Font light = new Font("Roboto Light", 0, 18);
        labelHandle.setFont(light);
        labelHandle.setForeground(Color.decode("#232323"));

        textHandle = new JTextField();
        textHandle.setPreferredSize(new Dimension(240, 30));
        textHandle.setFont(light);

        JLabel labelPassword = new JLabel("Password");
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

        Font normal = new Font("Roboto", 0, 18);

        buttonLogIn = new JButton("Log In / Sign In");
        buttonLogIn.setContentAreaFilled(false);
        buttonLogIn.setFocusPainted(false);
        buttonLogIn.setFont(normal);

        panelFooter.add(buttonLogIn);

        // BUILDER
        this.add(panelCenter, BorderLayout.CENTER);
        this.add(panelFooter, BorderLayout.SOUTH);
    }

    PanelLogin setChatGUI(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
        buttonLogIn.addActionListener(e -> actionButton());
        return this;
    }

    private void actionButton() {
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
        else
            chatGUI.loginRequest(usr, pwd);
    }
}