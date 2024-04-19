package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

import GraphicAPI.GeometricShapes.Cercle;
import GraphicAPI.GeometricShapes.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GUI extends JFrame {
    ArrayList<Cercle> cercles;
    ArrayList<GeometricShapes.Rectangle> rectangles;
    private JFrame frame;
    private String selectedShape;
    private int x1,y1; // Coordonnées du premier point
    private boolean firstClickDone = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new GUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void paint(JPanel drawingPanel) {
        for (Cercle cercle : cercles) {
            cercle.paint(drawingPanel);
        }
        for (Rectangle rectangle : rectangles) {
            rectangle.paint(drawingPanel);
        }
    }

    public GUI() {
        cercles = new ArrayList<>();
        rectangles = new ArrayList<>();
        selectedShape = "Rectangle";
        initialize();
    }

    public void initialize() {
        
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setFloatable(false);
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(Box.createHorizontalGlue());

        JToggleButton newButtonSelect = new JToggleButton("Select");
        JToggleButton newButtonAdd = new JToggleButton("Add");
        JToggleButton newButtonDiff = new JToggleButton("Difference");
        JToggleButton newButtonUnion = new JToggleButton("Union");

        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
        toolBar.add(newButtonDiff);
        toolBar.add(newButtonUnion);

        toolBar.addSeparator();

        JComboBox<String> shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Circle", "Triangle"});
        toolBar.add(shapeComboBox);

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = (String) shapeComboBox.getSelectedItem();
                System.out.println("Selected Shape: " + selectedShape);
            }
        });

        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
        toolBar.setBorder(BorderFactory.createCompoundBorder(toolBar.getBorder(), bottomBorder));

        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE);
        frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        drawingPanel.addMouseListener(new MouseListener() {
        	int x2, y2;
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
            	
                
                if (selectedShape.equals("Circle")) {
                    if (!firstClickDone) {
                	System.out.println(e.getX());
                    x1=e.getX();
                    y1=e.getY();
                    int[] a = {e.getX() - 25, e.getY() - 25, 50};
                    GeometricShapes geometricShapes = new GeometricShapes();
                    GeometricShapes.Cercle nouveauCercle = geometricShapes.new Cercle(a);
                    //cercles.add(nouveauCercle);
                    firstClickDone = true;
                    paint(drawingPanel);
                   
                    } else { 
                    	int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));
                    	int[] circleParams = {x1 - radius, y1 - radius, radius * 2};
                    	GeometricShapes geometricShapes = new GeometricShapes();
                    GeometricShapes.Cercle nouveauCercle = geometricShapes.new Cercle(circleParams);
                    cercles.add(nouveauCercle);
                    paint(drawingPanel);
                    firstClickDone = false;
                    }
                }
                    
                    
                    else if (selectedShape.equals("Rectangle")) {
                    System.out.println(e.getX());
                    System.out.println(e.getY());
                    if (x1 == 0 && y1 == 0) {
                    	x1=e.getX();
                        y1=e.getY();
                    } else {
                        x2 = e.getX();
                        y2 = e.getY();
                        int width = Math.abs(x2 - x1);
                        int height = Math.abs(y2 - y1);
                        int[] a = {Math.min(x1, x2), Math.min(y1, y2), width, height};
                        GeometricShapes geometricShapes = new GeometricShapes();
                        GeometricShapes.Rectangle nouveauRectangle = geometricShapes.new Rectangle(a);
                        rectangles.add(nouveauRectangle);
                        x1 = y1 = x2 = y2 = 0; // Réinitialiser pour la prochaine fois
                        paint(drawingPanel);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	
            }

            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            
        });
        drawingPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            	
            	 if (selectedShape.equals("Circle") && firstClickDone && isMouseInsideDrawingArea(e)) {
                     // Dessiner un fond blanc pour effacer les aperçus précédents
                     Graphics g = drawingPanel.getGraphics();
                     g.setColor(Color.WHITE);
                     g.fillRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());

                     // Redessiner les cercles précédemment dessinés
                     paint(drawingPanel);

                     // Calculer le rayon du cercle en fonction de la position actuelle de la souris
                     int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));

                     // Dessiner le cercle prévisualisé
                     g.setColor(Color.GREEN);
                     g.drawOval(x1 - radius, y1 - radius, radius * 2, radius * 2);
                     
                 }
            }

			private boolean isMouseInsideDrawingArea(MouseEvent e) {
				// TODO Auto-generated method stub
				return e.getX() >= 0 && e.getX() <= drawingPanel.getWidth() && e.getY() >= 0 && e.getY() <= drawingPanel.getHeight();			}
        });
        
        
    }
}
