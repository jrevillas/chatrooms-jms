package clientGUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class MyTableCellRenderer extends DefaultTableCellRenderer {
    private int color;
    private HashMap<String, Color> colorMap;
    private boolean center;

    public MyTableCellRenderer(int color, boolean center) {
        this.color = color;
        if (color == 2)
            colorMap = new HashMap<> ();
        this.center = center;
    }

    @Override
    public int getHorizontalAlignment() {
        if (center)
            return JLabel.CENTER;
        return JLabel.LEFT;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        if (color == 3) {
            if (0 == (int) table.getValueAt(row, column))
                this.setForeground(Color.WHITE);
            else
                this.setForeground (Color.BLACK);
        }
        if (color == 1)
            this.setForeground ( Color.GRAY );
        else if (color == 2) {
            String valor = (String) table.getValueAt ( row, column );
            Color color = colorMap.get ( valor );
            if (color == null) {
                color = colorAleatorio ();
                colorMap.put ( valor, color );
            }
            this.setForeground ( color );
        }

        setBorder ( BorderFactory.createCompoundBorder ( getBorder (), BorderFactory.createEmptyBorder ( 0, 4, 0 ,0 ) ) );
        return this;
    }

    private Color colorAleatorio() {
//        Color[] array = {Color.black, Color.blue, Color.cyan, Color.GREEN, Color.magenta, Color.YELLOW, Color.red};
//        return array[(int) (Math.random () * array.length)];
//        Random random = new Random (  );
//        final float hue = random.nextFloat();
//        // Saturation between 0.1 and 0.3
//        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
//        final float luminance = 0.9f;
//        return Color.getHSBColor(hue, saturation, luminance);
        Random random = new Random ();
        final float hue = random.nextFloat ();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
        return Color.getHSBColor ( random.nextFloat (), 0.9f, 1.0f );
//        return new Color( random.nextInt (0xFFFFFF));
    }
}
