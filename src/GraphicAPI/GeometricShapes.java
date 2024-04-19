package GraphicAPI;

import javax.swing.*;
import java.awt.*;

public class GeometricShapes extends JFrame {

    public GeometricShapes() {
        setTitle("Geometric Shapes");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ShapesPanel shapesPanel = new ShapesPanel();
        add(shapesPanel);

        setVisible(true);
    }

    class ShapesPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawShapes(g);
        }

        private void drawShapes(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            // Draw rectangle
            g2d.setColor(Color.BLACK);
            g2d.fillRect(50, 50, 100, 80);

            // Draw circle
            g2d.setColor(Color.BLACK);
            g2d.fillOval(200, 50, 100, 100);

            // Draw ellipse
            g2d.setColor(Color.BLACK);
            g2d.fillOval(350, 50, 120, 80);

            // Draw triangle
            int[] xPoints = {500, 550, 450};
            int[] yPoints = {200, 250, 250};
            g2d.setColor(Color.BLACK);
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GeometricShapes::new);
    }
    */
}
