package GraphicAPI;

import javax.swing.*;

import GraphicAPI.GUI.OperationType;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


public class GeometricShapes extends JFrame {
    private static final long serialVersionUID = 1L;

	interface Shape {
        boolean contains(Point point);
        java.awt.Rectangle getBounds();
        void setBounds(int dx, int dy);
        java.awt.Shape getShape(); // Obtenir la forme géométrique

        int getX(); // Obtenez la coordonnée x
        int getY(); // Obtenez la coordonnée y
        int getWidth(); // Obtenez la largeur de la forme
        int getHeight(); // Obtenez la hauteur de la forme
		void resize(int dx, int dy);
    }

    class Circle implements Shape, Serializable {
        private static final int MIN_RADIUS = 0;
		private int x;
        private int y;
        private int r;
        private Color color;
        private Area shape;

        public Circle(Area shape) {
            this.shape = shape;
        }

        public java.awt.Shape getShape() {
            return new Ellipse2D.Double(x, y, r, r);
        }

        public Circle(int[] args) {
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
            g2d.setColor(color);
            g2d.fillOval(x, y, r, r);
        }

        public boolean contains(Point point) {
            int centerX = x + r / 2;
            int centerY = y + r / 2;
            double distance = Math.sqrt(Math.pow(point.getX() - centerX, 2) + Math.pow(point.getY() - centerY, 2));
            return distance <= r / 2;
        }

        public java.awt.Rectangle getBounds() {
            int width = 2 * r;
            int height = 2 * r;
            return new java.awt.Rectangle(x, y, width, height);
        }

        public void setBounds(int dx, int dy) {
            this.x += dx;
            this.y += dy;
        }

        public void paintWithSelectionBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke(); // Sauvegarde de l'ancien style de trait
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); // Création du style de trait en pointillés
            g2d.setColor(Color.GRAY);
            g2d.drawOval(x, y, r, r);
            g2d.setStroke(oldStroke); // Restauration de l'ancien style de trait
        }

        public void paintWithOperationBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke(); // Sauvegarde de l'ancien style de trait
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); // Création du style de trait en pointillés
            g2d.setColor(Color.RED);
            g2d.drawOval(x, y, r, r);
            g2d.setStroke(oldStroke);
        }

        public void resize(int dx, int dy) {
            // Calculer la distance euclidienne entre les nouveaux et anciens points de redimensionnement
            int distance = (int)Math.sqrt(dx * dx + dy * dy);
            
            // Vérifier la direction du redimensionnement
            if (dx < 0 || dy < 0) {
                // Diminution de la taille du cercle
                if (r - distance >= MIN_RADIUS) {
                    // Vérifier que le rayon résultant est supérieur ou égal au rayon minimum
                    r -= distance;
                } else {
                    // Si le rayon résultant est inférieur au rayon minimum, redimensionner au rayon minimum
                    r = MIN_RADIUS;
                }
            } else {
                // Augmentation de la taille du cercle
                r += distance;
            }
        }
        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getWidth() {
            return r; // La largeur du cercle est égale à son diamètre
        }

        @Override
        public int getHeight() {
            return r; // La hauteur du cercle est égale à son diamètre
        }
        
        public void clear() {
            // Effacer les données du cercle (par exemple, réinitialiser les coordonnées et le rayon)
            this.x = 0;
            this.y = 0;
            this.r = 0;
            this.color = null;
        }
    }

    public class Rectangle implements Shape, Serializable {
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

        public Rectangle(Area area) {
            // Obtenir les coordonnées du rectangle à partir de la zone résultante
            java.awt.Rectangle bounds = area.getBounds();
            this.x1 = bounds.x;
            this.y1 = bounds.y;
            this.width = bounds.width;
            this.height = bounds.height;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void paint(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics(); // Obtenir le contexte graphique du JPanel
            g2d.setColor(color);
            g2d.fillRect(x1, y1, width, height);
        }

        public void paintWithSelectionBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke(); // Sauvegarde de l'ancien style de trait
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); // Création du style de trait en pointillés
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x1, y1, width, height);
            g2d.setStroke(oldStroke); // Restauration de l'ancien style de trait
        }

        public boolean contains(Point point) {
            // Vérifier si le point est à l'intérieur des limites du rectangle
            return point.getX() >= x1 && point.getX() <= x1 + width &&
                    point.getY() >= y1 && point.getY() <= y1 + height;
        }

        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(x1, y1, width, height);
        }

        public void setBounds(int dx, int dy) {
            this.x1 += dx;
            this.y1 += dy;
        }

        public java.awt.Shape getShape() {
            return new Rectangle2D.Double(x1, y1, width, height);
        }

        public void paintWithOperationBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke(); // Sauvegarde de l'ancien style de trait
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); // Création du style de trait en pointillés
            g2d.setColor(Color.RED);
            g2d.drawRect(x1, y1, width, height);
            g2d.setStroke(oldStroke);
        }

        public void resize(int dx, int dy) {
            // Ajuster la largeur et la hauteur du rectangle
            width += dx;
            height += dy;
            // S'assurer que la largeur et la hauteur sont toujours positives
            width = Math.max(width, 0);
            height = Math.max(height, 0);
        }

        @Override
        public int getX() {
            return x1;
        }

        @Override
        public int getY() {
            return y1;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
        public void clear() {
            // Effacer les données du rectangle (par exemple, réinitialiser les coordonnées, la largeur et la hauteur)
            this.x1 = 0;
            this.y1 = 0;
            this.width = 0;
            this.height = 0;
            this.color = null;
        }
    }

    class ComplexShape implements Shape,Serializable {
    	private transient Area shape;
        private ArrayList<Shape> shapes;
        private OperationType operation;

        public ComplexShape(Area shape, ArrayList<Shape> shapes, OperationType operation) {
        	this.shape = shape != null ? shape : new Area();
            this.shapes = shapes;
            this.operation = operation;
        }
        public enum AreaOperation {
            UNION, INTERSECTION, DIFFERENCE
        }
        @Override
        public boolean contains(Point point) {
            return shape.contains(point);
        }

        @Override
        public java.awt.Rectangle getBounds() {
        	if (shape != null) {
                return shape.getBounds();
            } else {
                // Gérer le cas où shape est null
                return new java.awt.Rectangle(0, 0, 0, 0); // Retourner une valeur par défaut
            }
            
        }

        public void clearShapes() {
            // Effacer toutes les sous-formes de la forme complexe
        	
            shapes.clear();
            // Réinitialiser la zone de la forme complexe
            shape.reset();
        }

        public void setBounds(int dx, int dy) {
            // Parcourir toutes les sous-formes de la forme complexe
            for (Shape s : shapes) {
                // Déplacer chaque sous-forme en ajoutant les valeurs de dx et dy à ses coordonnées
                s.setBounds(dx, dy);
            }

            shape = new Area();
            applyOperation();
        }

        public void resize(int dx, int dy) {
            // Parcourir toutes les sous-formes de la forme complexe
            for (Shape s : shapes) {
                // Redimensionner chaque sous-forme
                s.resize(dx, dy);
            }

            shape = new Area();
            applyOperation();
        }

        
        private void applyOperation() {
            Area result = new Area(shapes.get(0).getShape()); // Initialiser avec la première forme

            switch (operation) {
                case UNION:
                    // Ne rien faire, car l'opération d'union est déjà appliquée par défaut
                	for (int i = 1; i < shapes.size(); i++) {
                        result.add(new Area(shapes.get(i).getShape()));
                    }
                    break;
                case INTERSECTION:
                    // Effectuer une intersection avec chaque sous-forme
                    for (int i = 1; i < shapes.size(); i++) {
                        result.intersect(new Area(shapes.get(i).getShape()));
                    }
                    break;
                case DIFFERENCE:
                    // Effectuer une différence avec chaque sous-forme après la première
                    for (int i = 1; i < shapes.size(); i++) {
                        result.subtract(new Area(shapes.get(i).getShape()));
                    }
                    break;
            }

            // Mettre à jour la forme complexe avec le résultat final
            shape = result;
        }
        
        @Override
        public java.awt.Shape getShape() {
            return shape;
        }

        // Autres méthodes nécessaires pour manipuler la forme complexe, si nécessaire

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.BLACK); // Définir la couleur de dessin
            g2d.fill(shape);
            // Dessiner la forme complexe
        }

        public void drawBorder(Graphics2D g2, JPanel drawingPanel) {
            // Vérifier si le JPanel est null
            if (drawingPanel == null) {
                System.out.println("Error: drawingPanel is null.");
                return;
            }

            // Sauvegarder la couleur actuelle
            Color prevColor = g2.getColor();
            // Définir la couleur de la bordure
            g2.setColor(Color.BLUE);

            // Parcourir toutes les formes simples contenues dans la forme complexe
            for (Shape shape : shapes) {
                // Vérifier si la forme simple est à l'intérieur de la forme complexe
                if (shape.getBounds().intersects(getBounds())) {
                    // Dessiner la bordure de la forme simple
                    if (shape instanceof Rectangle) {
                        Rectangle rectangle = (Rectangle) shape;
                        rectangle.paintWithSelectionBorder(drawingPanel);
                    } else if (shape instanceof Circle) {
                        Circle circle = (Circle) shape;
                        circle.paintWithSelectionBorder(drawingPanel);
                    }
                    // Ajoutez d'autres cas pour les formes supplémentaires si nécessaire
                }
            }

            // Restaurer la couleur précédente
            g2.setColor(prevColor);
        }

        public ArrayList<Shape> getShapes() {
            return shapes;
        }

        public void updateBounds(int dx, int dy) {
            // Créer une nouvelle zone pour la forme complexe mise à jour
            Area updatedShape = new Area();
            for (Shape subShape : shapes) {
                if (subShape instanceof Circle) {
                    Circle circle = (Circle) subShape;
                    Ellipse2D.Double ellipse = new Ellipse2D.Double(circle.getX() + dx, circle.getY() + dy, circle.getWidth(), circle.getHeight());
                    updatedShape.add(new Area(ellipse));
                } else if (subShape instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) subShape;
                    Rectangle2D.Double rect = new Rectangle2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    updatedShape.add(new Area(rect));
                }
                // Ajoutez d'autres conditions pour les autres types de formes si nécessaire
            }
            // Mettre à jour la zone de la forme complexe
            shape = updatedShape;
        }

        // Autres méthodes de la classe

        @Override
        public int getX() {
            return getBounds().x;
        }

        @Override
        public int getY() {
            return getBounds().y;
        }

        @Override
        public int getWidth() {
            return getBounds().width;
        }

        @Override
        public int getHeight() {
            return getBounds().height;
        }

        public void paintWithOperationBorder(JPanel drawingPanel) {
            // Obtenir le contexte graphique du JPanel
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            
            // Sauvegarder l'ancien style de trait
            Stroke oldStroke = g2d.getStroke();
            
            // Création du style de trait en pointillés
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            
            // Définir la couleur de la bordure
            g2d.setColor(Color.RED);
            
            // Dessiner la bordure de la forme complexe
            g2d.draw(shape);
            
            // Restaurer l'ancien style de trait
            g2d.setStroke(oldStroke);
        }

    }

}
