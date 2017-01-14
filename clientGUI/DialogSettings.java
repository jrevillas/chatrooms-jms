package clientGUI;

import clientGUI.resources.languages.Language;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

class DialogSettings extends JDialog {
    private JPanel contentPane;
    private JPasswordField passwordField;
    private JComboBox<String> comboLanguages;
    private JButton buttonLanguage;
    private JButton buttonPassword;
    private String handle;
    private ChatGUI gui;

    private Language lan;
    private DialogLoading dialogLoading;
    private JTextField fieldHandle;
    private JButton buttonHandle;
    private JLabel labelLogout;
    private JButton buttonLogout;

    DialogSettings() {
        // Initializers
        lan = new Language();
        buildPanel();
        setContentPane(contentPane);

        // Button Settings
        buttonLanguage.addActionListener(e -> {
                    setLanguage(comboLanguages.getSelectedIndex());
                    gui.setLanguage();
                }
        );

        buttonPassword.addActionListener(e -> {
            if (this.getPassword().length() > 0) {
                DynamicProducerGUI.messageChangePassword(handle, this.getPassword());
                dialogLoading = new DialogLoading();
            } else
                JOptionPane.showMessageDialog(this, lan.getProperty("setPwdLen"));
        });

        buttonHandle.addActionListener(e -> System.out.println("TODO"));

        buttonLogout.addActionListener(e -> gui.logout());

        // Frame Settings
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setTitle(lan.getProperty("setTitle"));
        this.setModal(true);
        this.setResizable(false);
    }

    private void buildPanel() {
        Font light = new Font("Roboto Light", 0, 16);
        Color grey7 = Color.decode("#333333");

        buttonLanguage = new JButton();
        buttonLanguage.setFont(light);
        buttonLanguage.setForeground(grey7);

        comboLanguages = new JComboBox<>();
        comboLanguages.setFont(light);
        comboLanguages.setForeground(grey7);

        passwordField = new JPasswordField();
        passwordField.setFont(light);
        passwordField.setForeground(grey7);

        buttonPassword = new JButton();
        buttonPassword.setFont(light);
        buttonPassword.setForeground(grey7);

        fieldHandle = new JTextField();
        fieldHandle.setFont(light);
        fieldHandle.setForeground(grey7);
        fieldHandle.setEnabled(false);

        buttonHandle = new JButton();
        buttonHandle.setFont(light);
        buttonHandle.setForeground(grey7);
        buttonHandle.setEnabled(false);

        labelLogout = new JLabel();
        labelLogout.setFont(new Font("Roboto Light", 0, 14));
        labelLogout.setForeground(grey7);

        buttonLogout = new JButton();
        buttonLogout.setFont(light);
        buttonLogout.setForeground(grey7);

        contentPane = new JPanel(new GridLayout(4, 2, 5, 5));
        contentPane.setBorder(new EmptyBorder(5, 20, 15, 20));

        contentPane.add(comboLanguages, 0);
        contentPane.add(buttonLanguage, 1);
        contentPane.add(passwordField, 2);
        contentPane.add(buttonPassword, 3);
        contentPane.add(fieldHandle, 4);
        contentPane.add(buttonHandle, 5);
        contentPane.add(labelLogout, 6);
        contentPane.add(buttonLogout, 7);
    }

    private String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    private void setLanguage(int index) {
        String lang;
        lan.setLanguage(index);
        buttonLanguage.setText(lan.getProperty("setButLan"));
        buttonPassword.setText(lan.getProperty("setButPwd"));
        buttonHandle.setText(lan.getProperty("setButHand"));
        labelLogout.setText(lan.getProperty("setLabLog") + " " + this.handle);
        buttonLogout.setText(lan.getProperty("setButLog"));

        comboLanguages.removeAllItems();
        for (int i = 0; (lang = lan.getProperty("language" + i)) != null; i++)
            comboLanguages.addItem(lang);
        comboLanguages.setSelectedIndex(Language.index);
    }

    void result(boolean status) {
        if (status) {
            dialogLoading.dispose();
            JOptionPane.showMessageDialog(this, lan.getProperty("seChPwdOK"));
        } else {
            dialogLoading.dispose();
            JOptionPane.showMessageDialog(this, lan.getProperty("seChPwdWr"));
        }
    }

    void setParams(ChatGUI chatGUI, String handle) {
        this.gui = chatGUI;
        this.handle = handle;
        this.setLanguage(0);
    }

    public static void main(String[] args) {
        DialogSettings dialogSettings = new DialogSettings();

        dialogSettings.setLocationRelativeTo(null);
        dialogSettings.setVisible(true);
    }
}
