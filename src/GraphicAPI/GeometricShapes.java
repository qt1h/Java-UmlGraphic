package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

import java.io.Serializable;

public class GeometricShapes extends JFrame {
    public class Cercle implements Serializable {
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

        public Rectangle getBounds() {
            int width = 2 * r;
            int height = 2 * r;
            return new Rectangle(new int[] {x, y, width, height});        }
    }

    public class Rectangle implements Serializable {
        private static final long serialVersionUID = 1L;
        private int x1;
        private int y1;
        private int width;
        private int height;
        private Color color;

        public Rectangle(int[] args) {
            this.x1 = args[0];
            this.y1 = args[1];
            this.width = args[2];
            this.height = args[3];
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
            g2d.fillRect(x1, y1, width, height);
            // Pas besoin de disposer, car Graphics2D est obtenu à partir du JPanel
        }
        
        public void paintWithSelectionBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            
            // Définissez le motif de trait en utilisant un tableau de flottants pour spécifier les longueurs des segments pleins et vides
            float[] dashPattern = {5f, 5f}; // Par exemple, alternez 5 pixels pleins avec 5 pixels vides

            // Créez un objet BasicStroke avec le motif de trait spécifié
            Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashPattern, 0);

            // Conservez l'ancienne Stroke
            Stroke oldStroke = g2d.getStroke();

            // Définissez la nouvelle Stroke avec le motif de trait
            g2d.setStroke(dashedStroke);

            // Dessinez le rectangle avec la bordure en trait discontinu
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x1, y1, width, height);

            // Rétablissez la Stroke précédente
            g2d.setStroke(oldStroke);
        }


        public boolean contains(Point point) {
            // Vérifier si le point est à l'intérieur des limites du rectangle
            return point.getX() >= x1 && point.getX() <= x1 + width &&
                   point.getY() >= y1 && point.getY() <= y1 + height;
        }

        // Dans la classe Rectangle (peut ne pas être nécessaire car vous avez déjà un Rectangle)
        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(x1, y1, width, height);
        }
        public boolean intersects(Rectangle rect) {
            return this.x1 < rect.x1 + rect.width &&
                   this.x1 + this.width > rect.x1 &&
                   this.y1 < rect.y1 + rect.height &&
                   this.y1 + this.height > rect.y1;
        }
    }
    
}
