package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

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
            g2d.fillOval(x, y, r, r);
            // Pas besoin de disposer, car Graphics2D est obtenu à partir du JPanel
        }


        public boolean contains(Point point) {
            int centerX = x + r / 2;
            int centerY = y + r / 2;
            double distance = Math.sqrt(Math.pow(point.getX() - centerX, 2) + Math.pow(point.getY() - centerY, 2));
            return distance <= r / 2;
        }
        public void paintNew(JPanel drawingPanel, Cercle cercle) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke stroke2 = new BasicStroke(8f);
            g2d.setStroke(stroke2);
            g2d.setColor(cercle.color);
            g2d.fillOval(cercle.x, cercle.y, cercle.r, cercle.r);
        }

    }
    public class  Rectangle implements Serializable {
        private static final long serialVersionUID = 1L;
		private int x1;
        private int y1;
        private int x2;
        private int y2;
        private Color color;
		

        public Rectangle(int[] args) {
            this.x1 = args[0];
            this.y1 = args[1];
            this.x2 = args[2];
            this.y2 = args[3];
            this.color = Color.BLACK;
        }
        
        public void setColor(Color color) {
            this.color = color;
        }

        public void paint(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics(); // Obtenir le contexte graphique du JPanel
            Stroke stroke2 = new BasicStroke(8f);
            g2d.setStroke(stroke2);
            g2d.setColor(color);
            g2d.fillRect(x1, y1, x2, y2);
                      // Pas besoin de disposer, car Graphics2D est obtenu à partir du JPanel
        }
        
        public boolean contains(Point point) {
            // Vérifier si le point est à l'intérieur des limites du rectangle
            return point.getX() >= x1 && point.getX() <= x1 + x2 &&
                   point.getY() >= y1 && point.getY() <= y1 + y2;
        }
        /*public boolean isPointInside(int px, int py) {

            int centerX = x + r / 2;
            int centerY = y + r / 2;
            double distance = Math.sqrt(Math.pow(px - centerX, 2) + Math.pow(py - centerY, 2));
            return distance <= r / 2;
        }*/

    }
}
