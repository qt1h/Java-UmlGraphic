package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class GeometricShapes extends JFrame {
    public class  Cercle implements Serializable {
        private static final long serialVersionUID = 1L;
		private int x;
        private int y;
        private int r;
        private Color color;

        public Cercle(int[] args) {
            this.x = args[0];
            this.y = args[1];
            this.r = args[2];
            this.color = Color.RED;
        }
        
        public void setColor(Color color) {
            this.color = color;
        }

        public void paint(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics(); // Obtenir le contexte graphique du JPanel
            Stroke stroke2 = new BasicStroke(8f);
            g2d.setStroke(stroke2);
            g2d.setColor(color);
            g2d.drawOval(x, y, r, r);
            // Pas besoin de disposer, car Graphics2D est obtenu à partir du JPanel
        }


        public boolean isPointInside(int px, int py) {

            int centerX = x + r / 2;
            int centerY = y + r / 2;
            double distance = Math.sqrt(Math.pow(px - centerX, 2) + Math.pow(py - centerY, 2));
            return distance <= r / 2;
        }

    }
}
