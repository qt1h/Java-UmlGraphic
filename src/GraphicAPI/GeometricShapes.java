package GraphicAPI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import GraphicAPI.GUI.OperationType;

public class GeometricShapes extends JFrame {
    private static final long serialVersionUID = 1L;

	interface Shape {
        boolean contains(Point point);
        java.awt.Rectangle getBounds();
        java.awt.Shape getShape(); // geometric shape
        void setBounds(int dx, int dy);
        int getX();
        int getY(); 
        int getWidth(); 
        int getHeight();
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
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics(); //graphic context
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
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); // dashed style
            g2d.setColor(Color.GRAY);
            g2d.drawOval(x, y, r, r);
            g2d.setStroke(oldStroke);
        }

        public void resize(int dx, int dy) {
            int distance = (int)Math.sqrt(dx * dx + dy * dy);
            if (dx < 0 || dy < 0) {
                if (r - distance >= MIN_RADIUS) {
                    r -= distance;
                } else {
                    r = MIN_RADIUS;
                }
            } else {
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
            return r;
        }

        @Override
        public int getHeight() {
            return r;
        }
        
        public void clear() {
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
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            g2d.setColor(color);
            g2d.fillRect(x1, y1, width, height);
        }

        public void paintWithSelectionBorder(JPanel drawingPanel) {
            Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0)); 
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x1, y1, width, height);
            g2d.setStroke(oldStroke); 
        }

        public boolean contains(Point point) {
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

        public void resize(int dx, int dy) {
            width += dx;
            height += dy;
            width = Math.max(width, 0); // >=0
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
            this.x1 = 0;
            this.y1 = 0;
            this.width = 0;
            this.height = 0;
            this.color = null;
        }
    }

    class ComplexShape implements Shape,Serializable {
    	private transient Area shape; // area not Serializable
        private ArrayList<Shape> subShapes;
        private OperationType operation;
        private ArrayList<Shape> UNDOshapes;

        public ComplexShape(Area shape, ArrayList<Shape> subShapes, OperationType operation) {
        	this.shape = shape != null ? shape : new Area();
            this.subShapes = subShapes;
            this.operation = operation;
        }
        public enum AreaOperation {
            UNION, INTERSECTION, DIFFERENCE
        }
        
        @Override
        public boolean contains(Point point) {
        	if (shape != null) 
            return shape.contains(point);
        	else return false;
        }

        @Override
        public java.awt.Rectangle getBounds() {
        	if (shape != null) {
                return shape.getBounds();
            } else {
                return new java.awt.Rectangle(0, 0, 0, 0); //default value
            }
            
        }

        public void clearShapes() {
            subShapes.clear();
            shape.reset();
        }

        public void setBounds(int dx, int dy) { // TODO merge setBounds & resize with an enum
            for (Shape s : subShapes) {
                s.setBounds(dx, dy);
            }

            shape = new Area();
            applyOperation();
        }

        public void resize(int dx, int dy) {
            for (Shape s : subShapes) {
                s.resize(dx, dy);
            }

            shape = new Area();
            applyOperation();
        }

        
        public void applyOperation() {
        	Area result=new Area();
        	result = new Area(subShapes.get(0).getShape()); 
           
			switch (operation) {
                case UNION:
                	for (int i = 1; i < subShapes.size(); i++) {
                        result.add(new Area(subShapes.get(i).getShape()));
                    }
                    break;
                case INTERSECTION:
                    for (int i = 1; i < subShapes.size(); i++) {
                        result.intersect(new Area(subShapes.get(i).getShape()));
                    }
                    break;
                case DIFFERENCE:
                    for (int i = 1; i < subShapes.size(); i++) {
                        result.subtract(new Area(subShapes.get(i).getShape()));
                    }
                    break;
            }

            shape = result;
        }
        
        @Override
        public java.awt.Shape getShape() {
            return shape;
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.BLACK);
            if (shape!=null)
            g2d.fill(shape);
        }

        public void drawBorder(Graphics2D g2, JPanel drawingPanel) {
            if (drawingPanel == null) {
                System.out.println("Error: drawingPanel is null.");
                return;
            }

            Color prevColor = g2.getColor();
            g2.setColor(Color.BLUE);

            for (Shape shape : subShapes) {
                if (shape.getBounds().intersects(getBounds())) {
                    if (shape instanceof Rectangle) {
                        Rectangle rectangle = (Rectangle) shape;
                        rectangle.paintWithSelectionBorder(drawingPanel);
                    } else if (shape instanceof Circle) {
                        Circle circle = (Circle) shape;
                        circle.paintWithSelectionBorder(drawingPanel);
                    } //TODO complex subShape would make too much borders displayed with recursion (limit to depth=2?)
                }
            }
            g2.setColor(prevColor);
        }

        public ArrayList<Shape> getShapes() {
            return subShapes;
        }

        public void updateBounds(int dx, int dy) { //TODO merge with setBounds/resize ? or use operation again (better)
            Area updatedShape = new Area();
            for (Shape subShape : subShapes) {
                if (subShape instanceof Circle) {
                    Circle circle = (Circle) subShape;
                    Ellipse2D.Double ellipse = new Ellipse2D.Double(circle.getX() + dx, circle.getY() + dy, circle.getWidth(), circle.getHeight());
                    updatedShape.add(new Area(ellipse));
                } else if (subShape instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) subShape;
                    Rectangle2D.Double rect = new Rectangle2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    updatedShape.add(new Area(rect));
                } 
            }
            shape = updatedShape;
        }

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

		public ArrayList<Shape> getUNDOshapes() {
			return UNDOshapes;
		}

		public void setUNDOshapes(ArrayList<Shape> uNDOshapes) {
			UNDOshapes = uNDOshapes;
		}

		public void setShapes(ArrayList<Shape> subShapes) {
			this.subShapes=subShapes;
			
		}

		public void setOperation(OperationType operation) {
			this.operation=operation;
			
		}

		public OperationType getOperation() {
			return operation;
		}

		public void setShape(Area shape) {
			this.shape=shape;
			
		}

    }

}
