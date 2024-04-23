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
    //ArrayList<Circle> cercles;
    //ArrayList<GeometricShapes.Rectangle> rectangles;
	ArrayList<Shape> shapes;
    ArrayList<Shape> groupShape; // à généraliser groupe de formes
    private JFrame frame;
    private String selectedShape;
    private int x1, y1, x2, y2,dx,dy;
    private boolean firstClickDone = false;
    private boolean deleteMode = false;
    private boolean selectMode = false;
    private boolean addMode = false;
    private boolean moveMode = false;
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
        x1 = y1 = x2 = y2 = dx= dy=0;
        // Ajoutez d'autres variables de coordonnées ici si nécessaire
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
            g.setColor(new Color(0, 0, 255, 100)); // Bleu semi-transparent
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
        JToggleButton newButtonMove = new JToggleButton("Move");
        JToggleButton newButtonDiff = new JToggleButton("Difference");
        JToggleButton newButtonUnion = new JToggleButton("Union");
        JToggleButton newButtonDel = new JToggleButton("Delete");
        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
        toolBar.add(newButtonMove);
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
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        if (shapes.get(i).contains(e.getPoint())) {
                            shapes.remove(i);
                            paint(drawingPanel);
                            return;
                        }
                    }
                }else if (selectMode) {
                    // Vérifier si le mode de sélection est activé
                    for (Shape shape : shapes) {
                        if (shape instanceof Rectangle && shape.contains(e.getPoint())) {
                            // Si le clic a été effectué sur un rectangle, ajoutez-le à la liste des formes sélectionnées
                            groupShape.add(shape);
                            // Redessinez le panneau pour afficher la sélection
                            paint(drawingPanel);
                            return; // Sortez de la boucle après avoir trouvé le premier rectangle sélectionné
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
                    if (deleteMode) { // Vérifier si le mode de suppression est activé
                        return; // Retourner sans exécuter le reste de la méthode si le mode de suppression est activé
                    }
                    else if (moveMode) {
                        firstClickDone=true;
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                        // Vérifier si le mode de sélection est activé
                        for (Shape shape : shapes) {
                            if (shape instanceof Rectangle && shape.contains(e.getPoint())) {
                                // Si le clic a été effectué sur un rectangle, ajoutez-le à la liste des formes sélectionnées
                                groupShape.add(shape);
                                // Redessinez le panneau pour afficher la sélection
                                paint(drawingPanel);
                                return; // Sortez de la boucle après avoir trouvé le premier rectangle sélectionné
                            }
                            if (shape instanceof Circle && shape.contains(e.getPoint())) {
                                // Si le clic a été effectué sur un rectangle, ajoutez-le à la liste des formes sélectionnées
                                groupShape.add(shape);
                                // Redessinez le panneau pour afficher la sélection
                                paint(drawingPanel);
                                return; // Sortez de la boucle après avoir trouvé le premier rectangle sélectionné
                            }
                        } 
                    }
                    if (addMode){
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
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectMode) {
                    selectShapesInArea(); // Mettre à jour les formes sélectionnées
                    paint(drawingPanel); // Redessiner pour afficher les formes sélectionnées avec le bord en gris ou noir
                } else {
                    if (!deleteMode && firstClickDone && addMode ) { // Si le mode de suppression est désactivé et le premier clic a été effectué
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
                if (moveMode && firstClickDone) {
                    firstClickDone = false;
                    resetCoordinates();
                    groupShape.clear();
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
                    selectShapesInArea(); // Mettre à jour les formes sélectionnées
                    paint(drawingPanel); // Redessiner pour afficher les formes sélectionnées avec le bord en gris ou noir
                } else {
                    if (!deleteMode && firstClickDone && addMode) { // Si le mode de suppression est désactivé et le premier clic a été effectué
                        paint(drawingPanel); // Redessiner pour afficher les formes précédentes pour les mettre à jour
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
                    if(moveMode && firstClickDone){
                        if (groupShape.size() > 0) {
                            int dx = e.getX() - x1;
                            int dy = e.getY() - y1;
                            System.out.println(x1);
                            System.out.println(y1);
                            for (Shape shape : groupShape) {
                                shape.setBounds(dx, dy);
                            }
                            x1 = e.getX();
                            y1 = e.getY();                            
                            paint(drawingPanel);
                        }
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
                selectMode=false;
                addMode=false;
                moveMode=false;
            }
        });    
        newButtonSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMode = newButtonSelect.isSelected();
                deleteMode=false;
                addMode=false;
                moveMode=false;
            }
        });
        newButtonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMode = newButtonAdd.isSelected();
                deleteMode=false;
                selectMode=false;
                moveMode=false;
                System.out.println("Add mode: " + addMode);
            }
        });
        newButtonMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveMode = newButtonMove.isSelected();
                deleteMode=false;
                selectMode=false;
                addMode=false;
            }
        });
    }

    private void selectShapesInArea() {
        groupShape.clear(); // Effacer la liste des formes sélectionnées précédemment
        int[] rectangleParams = {Math.min(selectStartX, selectEndX), Math.min(selectStartY, selectEndY),
                Math.abs(selectEndX - selectStartX), Math.abs(selectEndY - selectStartY)};
        GeometricShapes geometricShapes = new GeometricShapes();
        selectionRect = geometricShapes.new Rectangle(rectangleParams);
        
        for (Shape shape : shapes) {
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                // Vérifier si le cercle est contenu dans la zone de sélection
                if (isCircleInArea(circle)) {
                    groupShape.add(circle);
                }
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                // Vérifier si le rectangle intersecte la zone de sélection
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
        
        // Coordonnées du coin supérieur gauche du rectangle de sélection
        int areaX = Math.min(selectStartX, selectEndX);
        int areaY = Math.min(selectStartY, selectEndY);
        
        // Largeur et hauteur du rectangle de sélection
        int areaWidth = Math.abs(selectEndX - selectStartX);
        int areaHeight = Math.abs(selectEndY - selectStartY);
        
        // Coordonnées du coin inférieur droit du rectangle de sélection
        int areaEndX = areaX + areaWidth;
        int areaEndY = areaY + areaHeight;
        
        // Vérification si un point du cercle est dans la zone de sélection
        for (int x = Math.max(centerX - radius, areaX); x <= Math.min(centerX + radius, areaEndX); x++) {
            for (int y = Math.max(centerY - radius, areaY); y <= Math.min(centerY + radius, areaEndY); y++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius/2) {
                    return true;
                }
            }
        }
        return false;
    }

}
