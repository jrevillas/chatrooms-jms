package clientGUI;

import javax.swing.*;

/**
 * Created by migui on 22/11/2016.
 */
class DialogLoading extends JDialog{
    DialogLoading() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("resources/loading.gif"));
        JLabel label = new JLabel(icon);
        JPanel contentPane = new JPanel();
        contentPane.add(label);
        this.setUndecorated(true);
        this.add(contentPane);
        this.pack();
    }
}
