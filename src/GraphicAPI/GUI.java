package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

import GraphicAPI.GeometricShapes.Circle;
import GraphicAPI.GeometricShapes.Rectangle;
import GraphicAPI.GeometricShapes.Shape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GUI extends JFrame {
    ArrayList<Shape> shapes;
    ArrayList<Shape> groupShape;
    private JFrame frame;
    private String selectedShape;
    private int x1, y1, x2, y2, dx, dy;
    private boolean firstClickDone = false;
    private boolean deleteMode = false;
    private boolean selectMode = false;
    private boolean addMode = false;
    private boolean movingShape = false; // Ajout d'un indicateur pour le déplacement d'une forme
    private int selectedShapeIndex = -1;
    private int selectStartX, selectStartY, selectEndX, selectEndY;
    private Rectangle selectionRect = null;

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
        x1 = y1 = x2 = y2 = dx = dy = 0;
    }

    public void paint(JPanel drawingPanel) {
        Graphics g = drawingPanel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());
        for (Shape shape : shapes) {
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                circle.paint(drawingPanel);
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                rectangle.paint(drawingPanel);
            }
        }
        if (selectMode) {
            g.setColor(new Color(0, 0, 255, 100));
            int width = Math.abs(selectEndX - selectStartX);
            int height = Math.abs(selectEndY - selectStartY);
            int x = Math.min(selectStartX, selectEndX);
            int y = Math.min(selectStartY, selectEndY);
            g.fillRect(x, y, width, height);
        }
        g.setColor(Color.BLUE);
        for (Shape shape : groupShape) {
            if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                rectangle.paintWithSelectionBorder(drawingPanel);
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                circle.paintWithSelectionBorder(drawingPanel);
            }
        }
    }

    public GUI() {
        shapes = new ArrayList<>();
        groupShape = new ArrayList<>();
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
        JToggleButton newButtonDel = new JToggleButton("Delete");
        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
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
                deleteMode = false;
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
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        if (shapes.get(i).contains(e.getPoint())) {
                            shapes.remove(i);
                            paint(drawingPanel);
                            return;
                        }
                    }
                } else if (selectMode) {
                    for (Shape shape : shapes) {
                        if (shape.contains(e.getPoint())) {
                            groupShape.add(shape);
                            paint(drawingPanel);
                            return;
                        }
                    }
                } else if (movingShape) { // Ajout de la gestion du déplacement de la forme
                    for (Shape shape : groupShape) {
                        if (shape.contains(e.getPoint())) {
                            // Déplacer la forme en utilisant le décalage dx et dy
                            shape.setBounds(dx, dy);
                            movingShape = false;
                            groupShape.clear(); // Effacer la sélection après le déplacement
                            paint(drawingPanel);
                            return;
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (selectMode) {
                    paint(drawingPanel);
                    selectStartX = e.getX();
                    selectStartY = e.getY();
                    selectEndX = e.getX();
                    selectEndY = e.getY();
                } else {
                    if (deleteMode) {
                        return;
                    }
                    if (addMode) {
                        paint(drawingPanel);
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
                    } else if (groupShape.size() > 0) { // Gérer le déplacement d'une forme
                        boolean clickedInsideShape = false;
                        for (Shape shape : groupShape) {
                            if (shape.contains(e.getPoint())) {
                                clickedInsideShape = true;
                                break;
                            }
                        }
                        if (clickedInsideShape) {
                            // Enregistrer les coordonnées initiales de la souris par rapport à la forme
                            x1 = e.getX();
                            y1 = e.getY();
                            movingShape = true;
                        } else {
                            // Si la souris est cliquée à l'extérieur de la forme, réinitialiser la sélection
                            groupShape.clear();
                            selectMode = true;
                            selectStartX = e.getX();
                            selectStartY = e.getY();
                            selectEndX = e.getX();
                            selectEndY = e.getY();
                            // Vérifier si un autre mode est sélectionné
                            if (addMode || deleteMode) {
                                addMode = false;
                                deleteMode = false;
                            }
                            paint(drawingPanel);
                        }
                    } else {
                        // Si aucun mode n'est activé et que vous cliquez sur le panneau de dessin,
                        // activez le mode de sélection et réinitialisez les coordonnées
                        selectMode = true;
                        selectStartX = e.getX();
                        selectStartY = e.getY();
                        selectEndX = e.getX();
                        selectEndY = e.getY();
                        paint(drawingPanel);
                    }
                }
            
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectMode) {
                    selectShapesInArea();
                    selectMode = false; // Désactiver le mode de sélection
                    resetCoordinates();
                    paint(drawingPanel);
                } else {
                    if (!deleteMode && firstClickDone && addMode) {
                        if (selectedShape.equals("Circle")) {
                            int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));
                            int[] circleParams = {x1 - radius, y1 - radius, radius * 2};
                            GeometricShapes geometricShapes = new GeometricShapes();
                            GeometricShapes.Circle newCircle = geometricShapes.new Circle(circleParams);
                            shapes.add(newCircle);
                            paint(drawingPanel);
                        } else if (selectedShape.equals("Rectangle")) {
                            x2 = e.getX();
                            y2 = e.getY();
                            int width = Math.abs(x2 - x1);
                            int height = Math.abs(y2 - y1);
                            int[] rectangleParams = {Math.min(x1, x2), Math.min(y1, y2), width, height};

                            GeometricShapes geometricShapes = new GeometricShapes();
                            GeometricShapes.Rectangle newRectangle = geometricShapes.new Rectangle(rectangleParams);
                            shapes.add(newRectangle);
                            paint(drawingPanel);
                        }

                        firstClickDone = false;
                    }
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
                if (selectMode) {
                    selectEndX = e.getX();
                    selectEndY = e.getY();
                    selectShapesInArea();
                    paint(drawingPanel);
                } else {
                    if (!deleteMode && firstClickDone && addMode) {
                        paint(drawingPanel);
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
                    } else if (movingShape) { // Déplacer la forme sélectionnée
                        // Calculer le décalage dx et dy pour le déplacement
                        dx = e.getX() - x1;
                        dy = e.getY() - y1;
                        // Mettre à jour les coordonnées de la forme sélectionnée
                        for (Shape shape : groupShape) {
                            shape.setBounds(dx,dy);
                        }
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
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
                selectMode = false;
                addMode = false;
                movingShape = false; // Désactiver le déplacement lors de la suppression
                if (deleteMode) {
                    // Supprimer les formes sélectionnées
                    shapes.removeAll(groupShape);
                    groupShape.clear(); // Effacer la sélection en mode delete
                }
               
                selectStartX = selectStartY = selectEndX = selectEndY = 0;// Effacer la sélection en mode delete
                
                paint(drawingPanel);
                newButtonAdd.setSelected(false);
                
            }
        });

        newButtonSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMode = newButtonSelect.isSelected();
                newButtonAdd.setSelected(false);
                deleteMode = false;
                addMode = false;
                movingShape = false; // Désactiver le déplacement lors de la sélection
                groupShape.clear(); // Effacer la sélection en mode sélection
                selectStartX = selectStartY = selectEndX = selectEndY = 0;
                paint(drawingPanel);
                
            }
        });

        newButtonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMode = newButtonAdd.isSelected();
                newButtonDel.setSelected(false);
                newButtonSelect.setSelected(false);
                deleteMode = false;
                selectMode = false;
                movingShape = false; // Désactiver le déplacement lors de l'ajout
                groupShape.clear(); // Effacer la sélection en mode ajout
                paint(drawingPanel);
                
            }
        });

        // Méthode pour désélectionner tous les boutons
      
    }
    
    private void selectShapesInArea() {
        groupShape.clear();
        int[] rectangleParams = {Math.min(selectStartX, selectEndX), Math.min(selectStartY, selectEndY),
                Math.abs(selectEndX - selectStartX), Math.abs(selectEndY - selectStartY)};
        GeometricShapes geometricShapes = new GeometricShapes();
        GeometricShapes.Rectangle selectionRect = geometricShapes.new Rectangle(rectangleParams);


        for (Shape shape : shapes) {
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                if (isCircleInArea(circle)) {
                    groupShape.add(circle);
                }
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                if (rectangle.getBounds().intersects(selectionRect.getBounds())) {
                    groupShape.add(rectangle);
                }
            }
        }
    }

    private boolean isCircleInArea(Circle circle) {
        int centerX = circle.getCenterX();
        int centerY = circle.getCenterY();
        int radius = circle.getRadius();

        int areaX = Math.min(selectStartX, selectEndX);
        int areaY = Math.min(selectStartY, selectEndY);

        int areaWidth = Math.abs(selectEndX - selectStartX);
        int areaHeight = Math.abs(selectEndY - selectStartY);

        int areaEndX = areaX + areaWidth;
        int areaEndY = areaY + areaHeight;

        for (int x = Math.max(centerX - radius, areaX); x <= Math.min(centerX + radius, areaEndX); x++) {
            for (int y = Math.max(centerY - radius, areaY); y <= Math.min(centerY + radius, areaEndY); y++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius / 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
