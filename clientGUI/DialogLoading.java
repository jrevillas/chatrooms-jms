package clientGUI;

import javax.swing.*;

class DialogLoading extends JDialog{
    DialogLoading() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("resources/loading.gif"));
        JLabel label = new JLabel(icon);
        JPanel contentPane = new JPanel();
        contentPane.add(label);
        this.setUndecorated(true);
        this.add(contentPane);
        this.pack();
        this.setAlwaysOnTop(true);
        this.setResizable(false);
    }
}
