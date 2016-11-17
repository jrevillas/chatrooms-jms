package clientGUI;

import javax.swing.*;
import java.awt.event.*;

public class DialogSettings extends JDialog {
    private JPanel contentPane;
    private JTextField fieldHandler;
    private JButton buttonHandler;
    private JButton buttonPassword;
    private JButton buttonLogout;
    private JPasswordField passwordField;
    private int result;

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
        setContentPane(contentPane);
        setModal(true);

        buttonHandler.addActionListener(e -> {
            result = 1;
            dispose();
        });

        buttonPassword.addActionListener(e -> {
            if (this.getPassword().length() > 0) {
                result = 2;
                dispose();
            }
            JOptionPane.showMessageDialog(this, "You haven't entered any password");
        });

        buttonLogout.addActionListener(e ->  {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?") == 0) {
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
}
