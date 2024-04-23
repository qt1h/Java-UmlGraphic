package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

import java.io.Serializable;

public class GeometricShapes extends JFrame {
	interface Shape {
	    boolean contains(Point point);
	    java.awt.Rectangle getBounds();
        void setBounds(int dx, int dy);
	}

	class Circle implements Shape, Serializable {
		private int x;
		private int y;
		private int r;
		private Color color;

		public Circle(int[] args) {
			this.x = args[0];
			this.y = args[1];
			this.r = args[2];
			this.color = Color.RED;
		}
		
		public int getCenterX() {
			return x + r / 2;
		}

		public int getCenterY() {
			return y + r / 2;
		}

		public int getRadius() {
			return r;
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
	}

	class Rectangle implements Shape, Serializable {
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

		public int getX1() {
			return x1;
		}

		public int getY1() {
			return y1;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
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
	}
    
}
