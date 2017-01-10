package clientGUI;

import clientGUI.resources.languages.Language;

import javax.swing.*;
import java.awt.event.*;

class DialogSettings extends JDialog {
    private JPanel contentPane;
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
    private DialogLoading dialogLoading;

    int getResult() {
        return result;
    }

    String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    DialogSettings() {
        lan = new Language();
        setContentPane(contentPane);
        setModal(true);
        this.setLanguage(0);

        buttonLanguage.addActionListener(e -> {
            result = 1;
            setLanguage(comboBox1.getSelectedIndex());
            dialogLoading = new DialogLoading();
        });

        buttonHandler.addActionListener(e -> {
            result = 2;
            dialogLoading = new DialogLoading();
        });

        buttonPassword.addActionListener(e -> {
            if (this.getPassword().length() > 0) {
                DynamicProducerGUI.messageChangePassword(ChatGUI.user.getHandle(), this.getPassword());
                dialogLoading = new DialogLoading();
            }
            JOptionPane.showMessageDialog(this, lan.getProperty("setPwdError"));
        });

        buttonLogout.addActionListener(e ->  {
            if (JOptionPane.showConfirmDialog(this, lan.getProperty("setLogConf")) == 0) {
                result = 2;
                dialogLoading = new DialogLoading();
            }
        });

        buttonExit.addActionListener(e -> dispose());

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
        labelLog.setText(lan.getProperty("setLabLog") + ChatGUI.user.getHandle());
        buttonLogout.setText(lan.getProperty("setButLog"));
        buttonExit.setText(lan.getProperty("exit"));
        comboBox1.removeAllItems();
        for (int i = 0; (lang = lan.getProperty("language" + i)) != null ; i++)
            comboBox1.addItem(lang);
        comboBox1.setSelectedIndex(Language.index);
    }

    void result(boolean status) {
        if (status){
            dialogLoading.dispose();
            JOptionPane.showMessageDialog(this, lan.getProperty("seChPwdOK"));
        } else {
            dialogLoading.dispose();
            JOptionPane.showMessageDialog(this, lan.getProperty("seChPwdWr"));
        }
    }
}
