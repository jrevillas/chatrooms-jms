package clientGUI;

import clientGUI.resources.languages.Language;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

class DialogChangeName extends JDialog {
    private Language lan;
    private JTextField textName;
    private String result = null;

    DialogChangeName() {
        lan = new Language();
        this.setLayout(new BorderLayout());

        // CENTER
        JPanel panelCenter = new JPanel(new GridLayout(2,1));
        panelCenter.setBorder(new EmptyBorder(5, 20, 15, 20));

        JLabel labelRoom = new JLabel(lan.getProperty("chRoom"));
        Font light = new Font("Roboto Light", 0, 18);
        labelRoom.setFont(light);
        labelRoom.setForeground(Color.decode("#232323"));

        textName = new JTextField();
        textName.setPreferredSize(new Dimension(240, 30));
        textName.setFont(light);

        panelCenter.add(labelRoom, 0);
        panelCenter.add(textName, 1);

        // FOOTER
        JPanel panelFooter = new JPanel(new GridLayout(1, 2));

        JButton buttonCancel = new JButton(lan.getProperty("cancel"));
        buttonCancel.setContentAreaFilled(false);
        buttonCancel.setFocusPainted(false);
        Font normal = new Font("Roboto", 0, 18);
        buttonCancel.setFont(normal);
        buttonCancel.setMargin(new Insets(5, 0, 5, 0));
        buttonCancel.addActionListener(e -> onCancel());


        JButton buttonOK = new JButton(lan.getProperty("ok"));
        buttonOK.setContentAreaFilled(false);
        buttonOK.setFocusPainted(false);
        buttonOK.setFont(normal);
        buttonOK.addActionListener(e -> onOK());


        panelFooter.add(buttonCancel, 0);
        panelFooter.add(buttonOK, 1);

        // BUILDER
        this.add(panelCenter, BorderLayout.CENTER);
        this.add(panelFooter, BorderLayout.SOUTH);

        // FRAME SETTINGS
        ((JPanel)this.getContentPane()).registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ((JPanel)this.getContentPane()).registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(lan.getProperty("chTitle"));
        this.pack();
        this.setResizable(false);
        this.setModal(true);
    }

    private void onOK(){
        String topicName = textName.getText();
        if (topicName.length () > 16)
            JOptionPane.showMessageDialog(this, lan.getProperty("crNameLeng"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        else if (topicName.length () == 0)
            JOptionPane.showMessageDialog(this, lan.getProperty("crEmptyName"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        else {
            result = topicName;
            this.setVisible(false);
            dispose();
        }
    }

    private void onCancel(){
        this.setVisible(false);
        this.dispose();
    }

    String getResult() {
        return result;
    }
}
