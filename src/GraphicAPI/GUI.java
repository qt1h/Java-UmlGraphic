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
    private int x1, y1, x2, y2;
    private boolean firstClickDone = false;
    private boolean deleteMode = false;
    private int selectedShapeIndex = -1;

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

    public void resetCoordinates() {
        x1 = y1 = x2 = y2 = 0;
        // Ajoutez d'autres variables de coordonnées ici si nécessaire
    }

    public void paint(JPanel drawingPanel) {
        Graphics g = drawingPanel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());
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
        JToggleButton newButtonDel = new JToggleButton("Delete");
        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
        toolBar.add(newButtonDiff);
        toolBar.add(newButtonUnion);
        toolBar.add(newButtonDel);

        toolBar.addSeparator();

        JComboBox<String> shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Circle", "Triangle"});
        toolBar.add(shapeComboBox);

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCoordinates();
                firstClickDone = false;
                selectedShape = (String) shapeComboBox.getSelectedItem();
                System.out.println("Selected Shape: " + selectedShape);
                deleteMode = false; // Désactiver le mode de suppression lorsque la forme est modifiée
            }
        });

        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
        toolBar.setBorder(BorderFactory.createCompoundBorder(toolBar.getBorder(), bottomBorder));

        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE);
        frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        drawingPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (deleteMode) {
                    // Vérifier si l'utilisateur a cliqué sur une forme existante
                    for (int i = cercles.size() - 1; i >= 0; i--) {
                        if (cercles.get(i).contains(e.getPoint())) {
                            cercles.remove(i);
                            paint(drawingPanel);
                            return;
                        }
                    }
                    for (int i = rectangles.size()-1; i >=0 ; i--) {
                        if (rectangles.get(i).contains(e.getPoint())) {
                            rectangles.remove(i);
                            paint(drawingPanel);
                            return;
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (deleteMode) { // Vérifier si le mode de suppression est activé
                    return; // Retourner sans exécuter le reste de la méthode si le mode de suppression est activé
                }

                if (selectedShape.equals("Circle")) {
                    if (!firstClickDone) {
                        x1 = e.getX();
                        y1 = e.getY();
                        firstClickDone = true;
                    }
                } else if (selectedShape.equals("Rectangle")) {
                    if (!firstClickDone) {
                        x1 = e.getX();
                        y1 = e.getY();
                        firstClickDone = true;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!deleteMode && firstClickDone) { // Si le mode de suppression est désactivé et le premier clic a été effectué
                    if (selectedShape.equals("Circle")) {
                        int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));
                        int[] circleParams = {x1 - radius, y1 - radius, radius * 2};
                        GeometricShapes geometricShapes = new GeometricShapes();
                        GeometricShapes.Cercle nouveauCercle = geometricShapes.new Cercle(circleParams);
                        cercles.add(nouveauCercle);
                        paint(drawingPanel);
                    } else if (selectedShape.equals("Rectangle")) {
                        x2 = e.getX();
                        y2 = e.getY();
                        int width = Math.abs(x2 - x1);
                        int height = Math.abs(y2 - y1);
                        int[] rectangleParams = {Math.min(x1, x2), Math.min(y1, y2), width, height};
                        GeometricShapes geometricShapes = new GeometricShapes();
                        GeometricShapes.Rectangle nouveauRectangle = geometricShapes.new Rectangle(rectangleParams);
                        rectangles.add(nouveauRectangle);
                        paint(drawingPanel);
                    }
                    firstClickDone = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        drawingPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!deleteMode && firstClickDone) { // Si le mode de suppression est désactivé et le premier clic a été effectué
                    paint(drawingPanel); // Redessiner les formes précédentes pour les mettre à jour
                    if (selectedShape.equals("Circle")) {
                        int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));
                        Graphics g = drawingPanel.getGraphics();
                        g.setColor(Color.GREEN);
                        g.drawOval(x1 - radius, y1 - radius, radius * 2, radius * 2);
                    } else if (selectedShape.equals("Rectangle")) {
                        int x = Math.min(x1, e.getX());
                        int y = Math.min(y1, e.getY());
                        int width = Math.abs(e.getX() - x1);
                        int height = Math.abs(e.getY() - y1);
                        Graphics g = drawingPanel.getGraphics();
                        g.setColor(Color.GREEN);
                        g.drawRect(x, y, width, height);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        newButtonDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMode = newButtonDel.isSelected();
            }
        });
    }
}
