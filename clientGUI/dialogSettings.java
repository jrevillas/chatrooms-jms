package clientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class DialogSettings extends JDialog {
    private JPanel contentPane;
    private JTextField fieldHandler;
    private JButton buttonHandler;
    private JButton buttonPassword;
    private JButton buttonLogout;
    private JPasswordField passwordField;
    private JComboBox<String> comboBox1;
    private JButton buttonLanguage;
    private JLabel labelLog;
    private JButton buttonExit;
    private JPanel panelGeneral;
    private int result;
    private Language lan;

    int getResult() {
        return result;
    }

    String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    String getHandler() {
        return fieldHandler.getText();
    }

    DialogSettings() {
        lan = new Language();
        setContentPane(contentPane);
        setModal(true);
        this.setLanguage(0);

        buttonLanguage.addActionListener(e -> {
            result = 1;
            setLanguage(comboBox1.getSelectedIndex());
        });

        buttonHandler.addActionListener(e -> {
            result = 1;
        });

        buttonPassword.addActionListener(e -> {
            if (this.getPassword().length() > 0) {
                result = 2;
            }
            JOptionPane.showMessageDialog(this, lan.getProperty("setPwdError"));
        });

        buttonLogout.addActionListener(e ->  {
            if (JOptionPane.showConfirmDialog(this, lan.getProperty("setLogConf")) == 0) {
                result = 3;
                dispose();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setLanguage(int index) {
        String lang;
        lan.setLanguage(index);
        panelGeneral.setBorder(BorderFactory.createTitledBorder(lan.getProperty("settings")));
        buttonLanguage.setText(lan.getProperty("setButLan"));
        buttonHandler.setText(lan.getProperty("setButHand"));
        buttonPassword.setText(lan.getProperty("setButPwd"));
        // TODO
        //labelLog.setText(lan.getProperty("setLabLog") + " " + ChatGUI.user.getHandle());
        labelLog.setText(lan.getProperty("setLabLog") + "mnunezdm");
        buttonLogout.setText(lan.getProperty("setButLog"));
        buttonExit.setText(lan.getProperty("exit"));
        comboBox1.removeAllItems();
        for (int i = 0; (lang = lan.getProperty("language" + i)) != null ; i++)
            comboBox1.addItem(lang);
        comboBox1.setSelectedIndex(Language.index);
    }

    public static void main(String[] args) {
        DialogSettings dialog = new DialogSettings();
        dialog.setMinimumSize(new Dimension(375, 200));
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);

        LoginDialog dialoG = new LoginDialog();
        dialoG.setMinimumSize(new Dimension(300, 200));
        dialoG.setLocationRelativeTo(null);
        dialoG.setVisible(true);

        DialogJoinRoom dialoJ = new DialogJoinRoom();
        dialoJ.setMinimumSize(new Dimension(300, 200));
        dialoJ.setLocationRelativeTo(null);
        dialoJ.setVisible(true);
    }

}
