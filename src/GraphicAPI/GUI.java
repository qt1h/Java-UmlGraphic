package GraphicAPI;

import GraphicAPI.GeometricShapes.Rectangle;
import GraphicAPI.GeometricShapes.Circle;
import GraphicAPI.GeometricShapes.ComplexShape;
import GraphicAPI.GeometricShapes.Shape;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private boolean resizeMode = false;
    int handleSize = 10;
    private boolean movingShape = false; 
    private int selectStartX, selectStartY, selectEndX, selectEndY;
    // Enum to represent different operations
    private boolean undo=false;
    public enum OperationType {
        DIFFERENCE,
        UNION,
        INTERSECTION
    }
    public void serializeShape() {
    JFileChooser fileChooser = new JFileChooser();
    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(shapes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
   public void deserializeShape() {
    JFileChooser fileChooser = new JFileChooser();
    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
        	shapes = (ArrayList <Shape>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
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

    public boolean isNearBorder(GeometricShapes.Shape shape, Point clickPoint) {
        int threshold = 20; // distance from border
      
        return clickPoint.x <= shape.getX() + threshold || 
               clickPoint.x >= shape.getX() + shape.getWidth() - threshold ||
               clickPoint.y <= shape.getY() + threshold || 
               clickPoint.y >= shape.getY() + shape.getHeight() - threshold;
    }
    
    public boolean isPointInsideGroupShape(ArrayList<GeometricShapes.Shape> groupShape, Point point) {
		// Vérifier si la liste de formes est vide
		if (groupShape.isEmpty()) {
			return false;
		}

		// Récupérer la première forme du groupe (vous pouvez ajuster cette logique selon vos besoins)
		GeometricShapes.Shape selectedShape = groupShape.get(0);

		// Vérifier si le point est à l'intérieur de la forme
		return point.x >= selectedShape.getX() && point.x <= selectedShape.getX() + selectedShape.getWidth() &&
				point.y >= selectedShape.getY() && point.y <= selectedShape.getY() + selectedShape.getHeight();
	}
    public void paint(JPanel drawingPanel) {
        Graphics g = drawingPanel.getGraphics();
        if (g == null) {
            return; 
        }
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());

        /*ComplexShape lastComplexShape = null;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i) instanceof ComplexShape) {
                lastComplexShape = (ComplexShape) shapes.get(i);
                break;
            }
        }
        if (lastComplexShape != null)lastComplexShape.draw((Graphics2D) g);*/
        for (Shape shape : shapes) {
            if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                /*if (undo==false ) complexShape.clearShapes();*/
                complexShape.draw((Graphics2D) g); // Dessiner la forme complexe
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                circle.paint(drawingPanel);
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                rectangle.paint(drawingPanel);
            }
        }
        
        // selection  
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
            if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                complexShape.drawBorder((Graphics2D) g,drawingPanel); // Dessiner la forme complexe
            }
            
            else if(shape instanceof Rectangle) {
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
        deserializeShape();
        groupShape = new ArrayList<>();
       
        selectedShape = "Rectangle";
        initialize();
    }
    
    public void initialize() {

        frame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        int width = (int) (screenWidth * 0.5); 
        int height = (int) (screenHeight * 0.5); 
        int x = (screenWidth - width) / 2;
        int y = (screenHeight - height) / 2;
        
        frame.setBounds(x, y, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setFloatable(false);
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        JToggleButton newButtonSelect = new JToggleButton("<html><b>SELECT</b></html>");
        JToggleButton newButtonAdd = new JToggleButton("<html><b>ADD</b></html>");
        JToggleButton newButtonDel = new JToggleButton("<html><b>DELETE</b></html>");
        JToggleButton newButtonSave = new JToggleButton("<html><b>Save</b></html>");
        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
        toolBar.add(newButtonDel);
        toolBar.add(newButtonSave);

        toolBar.addSeparator();

        JButton differenceButton = new JButton("Difference");
        JButton unionButton = new JButton("Union");
        JButton intersectionButton = new JButton("Intersection");
        toolBar.add(differenceButton);
        toolBar.add(unionButton);
        toolBar.add(intersectionButton);

        toolBar.addSeparator();
        JToggleButton buttonUndo = new JToggleButton("<html><b>Undo</b></html>");
        toolBar.add(buttonUndo);
        
        toolBar.addSeparator();
        JComboBox<String> shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Circle"});
        toolBar.add(shapeComboBox);

        toolBar.addSeparator();
        JComboBox<String> prefabComboBox = new JComboBox<>(new String[]{"prefab1","prefab2"});
        toolBar.add(prefabComboBox);
        toolBar.setBackground(new Color(90, 90, 90));
       
        
        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCoordinates();
                selectedShape = (String) shapeComboBox.getSelectedItem();
                firstClickDone=false;
                if (!newButtonAdd.isSelected()) newButtonAdd.doClick();
            }
        });
        
        for (Component component : toolBar.getComponents()) { // comment vérifier qu'on passe sur les JComboBox?
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    toolBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    toolBar.setCursor(Cursor.getDefaultCursor());
                }
            });
        }

        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
        toolBar.setBorder(BorderFactory.createCompoundBorder(toolBar.getBorder(), bottomBorder));

        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        repaint();
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
                        	if (shape instanceof ComplexShape) {
                                ComplexShape complexShape = (ComplexShape) shape;
                                if (isComplexShapeSelected(complexShape, e.getPoint())) {
                                    groupShape.add(complexShape);
                                    paint(drawingPanel);
                                    return;
                                }
                            }
                        	else {
                                groupShape.add(shape);
                                paint(drawingPanel);
                                return;
                            }
                        }
                    }
                } else if (movingShape) {
                    for (Shape shape : groupShape) {
                        if (shape.contains(e.getPoint())) {
                            shape.setBounds(dx, dy);
                            movingShape = false;
                            groupShape.clear();
                            paint(drawingPanel);
                            return;
                        }
                    }
                }else {
                    // Logique de désélection des formes complexes
                    if (!isInsideAnyShape(e.getPoint())) {
                        groupShape.clear(); // Désélectionner toutes les formes complexes
                        paint(drawingPanel); // Redessiner pour mettre à jour l'affichage
                    }
                }
                
            }
            private boolean isInsideAnyShape(Point point) {
                for (Shape shape : shapes) {
                    if (shape.contains(point)) {
                        return true; // Le point est à l'intérieur d'une forme, pas de désélection
                    }
                }
                return false; // Aucune forme n'a été trouvée, désélectionner toutes les formes complexes
            }
                
            private boolean isComplexShapeSelected(ComplexShape complexShape, Point point) {
                    for (Shape subShape : complexShape.getShapes()) {
                        if (subShape.contains(point)) {
                            return true;
                        }
                    }
                    return false;
            }
          
            @Override
            public void mousePressed(MouseEvent e) {
                if (selectMode) {
                    // Si le mode de sélection est activé
                    resizeMode = true; // Activer le mode de redimensionnement par défaut
                    paint(drawingPanel); // Redessine pour effacer la sélection précédente
                    selectStartX = e.getX(); // Enregistre les coordonnées du clic initial
                    selectStartY = e.getY();
                    selectEndX = e.getX();
                    selectEndY = e.getY();
                } else {
                    if (deleteMode) {
                        // Si le mode de suppression est activé
                        for (int i = shapes.size() - 1; i >= 0; i--) {
                            // Parcourt les formes de la dernière à la première
                            if (shapes.get(i).contains(e.getPoint())) {
                                // Si la forme actuelle contient le point du clic
                                shapes.remove(i); // Supprime la forme
                                paint(drawingPanel); // Redessine pour mettre à jour l'affichage
                                return; // Sort de la méthode après avoir supprimé la première forme trouvée
                            }
                        }
                    }

                    if (addMode) {
                        // Si le mode d'ajout de forme est activé
                        paint(drawingPanel); // Redessine pour effacer toute forme précédente en cours de dessin
                        if (selectedShape.equals("Circle")) {
                            if (!firstClickDone) {
                                x1 = e.getX(); // Enregistre les coordonnées du premier clic
                                y1 = e.getY();
                                firstClickDone = true; // Marque le premier clic comme étant effectué
                            }
                        } else if (selectedShape.equals("Rectangle")) {
                            if (!firstClickDone) {
                                x1 = e.getX(); // Enregistre les coordonnées du premier clic
                                y1 = e.getY();
                                firstClickDone = true; // Marque le premier clic comme étant effectué
                            }
                        }
                    } else if (groupShape.size() > 0) {
                        // Si une forme du groupe est sélectionnée
                        boolean clickedInsideShape = false;
                        for (Shape shape : groupShape) {
                            // Vérifie si le clic est à l'intérieur d'une forme du groupe
                            if (shape.contains(e.getPoint())) {
                                clickedInsideShape = true;
                                break; // Sort de la boucle si une forme est trouvée
                            }
                        }
                        if (clickedInsideShape) {
                            // Si le clic est à l'intérieur d'une forme du groupe
                            x1 = e.getX(); // Enregistre les coordonnées du premier clic pour le déplacement
                            y1 = e.getY();
                            if (groupShape.size() == 0|| !isPointInsideGroupShape(groupShape, e.getPoint())) {
                    	        // Aucune forme dans le groupe
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    	    } else if (isNearBorder(groupShape.get(0), e.getPoint())) {
                    	        // La souris est proche du bord de la forme
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    	        resizeMode = true; // Activer le mode de redimensionnement
                                movingShape = false;
                    	    } else {
                    	        // La souris est à l'intérieur de la forme mais pas proche du bord
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    	        movingShape = true; // Activer le mode de déplacement de forme
                                resizeMode = false;
                    	    }
                 
                        } else {
                            // Si le clic est à l'extérieur de toutes les formes du groupe
                            groupShape.clear(); // Efface la sélection de groupe
                            if (newButtonSelect.isSelected()) selectMode = true; // Active le mode de sélection
                            selectStartX = e.getX(); // Enregistre les coordonnées du premier clic de sélection
                            selectStartY = e.getY();
                            selectEndX = e.getX();
                            selectEndY = e.getY();
                            if (addMode || deleteMode) {
                                // Désactive les modes d'ajout ou de suppression si l'un d'eux est actif
                                addMode = false;
                                deleteMode = false;
                            }
                            paint(drawingPanel); // Redessine pour afficher la zone de sélection
                        }
                    } else {
                        // Si aucune forme ou aucun groupe n'est sélectionné
                        selectMode = true; // Active le mode de sélection
                        selectStartX = e.getX(); // Enregistre les coordonnées du premier clic de sélection
                        selectStartY = e.getY();
                        selectEndX = e.getX();
                        selectEndY = e.getY();
                        paint(drawingPanel); // Redessine pour afficher la zone de sélection
                    }
                }
            }


            


            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectMode) {
                    selectShapesInArea();
                    selectMode = false;
                    resetCoordinates();
                    paint(drawingPanel);
                    movingShape=false;
                    resizeMode=true;
                } else {
                    if (!deleteMode && firstClickDone ) {
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
                        }  else if (movingShape) {
                            // Calculer le déplacement pour chaque sous-forme de la forme complexe sélectionnée
                            dx = e.getX() - x1;
                            dy = e.getY() - y1;
                            for (Shape shape : groupShape) {
                                shape.setBounds(dx, dy); // Déplacer chaque sous-forme
                            }
                            // Mettre à jour les limites de la forme complexe elle-même
                            ((ComplexShape) groupShape.get(0)).updateBounds(dx, dy);
                            x1 = e.getX();
                            y1 = e.getY();
                            paint(drawingPanel); // Redessiner pour afficher les formes déplacées
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
                    if (groupShape.size() == 0|| !isPointInsideGroupShape(groupShape, e.getPoint())) {
            	        // Aucune forme dans le groupe
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            	    } else if (isNearBorder(groupShape.get(0), e.getPoint())) {
            	        // La souris est proche du bord de la forme
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            	    } else {
            	        // La souris est à l'intérieur de la forme mais pas proche du bord
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            	    }
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
                    }
                    else if (resizeMode) {
                        int dx = e.getX() - x1;
                        int dy = e.getY() - y1;
                        for (Shape selectedShape : groupShape) {
                            if (selectedShape instanceof Circle) {
                                Circle circle = (Circle) selectedShape;
                                circle.resize(dx, dy);
                            } else if (selectedShape instanceof Rectangle) {
                                Rectangle rectangle = (Rectangle) selectedShape;
                                rectangle.resize(dx, dy);
                            }else if (selectedShape instanceof ComplexShape) {
                            	 ((ComplexShape) groupShape.get(0)).resize(dx, dy);
                            }
                            
                        }
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                    } else if (movingShape) {
                        int dx = e.getX() - x1;
                        int dy = e.getY() - y1;
                        for (Shape shape : groupShape) {
                            shape.setBounds(dx,dy);
                        }
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                    
                    }else {
                        // Mettre à jour les limites de la forme complexe elle-même
                        ((ComplexShape) groupShape.get(0)).updateBounds(dx, dy);
                        x1 = e.getX();
                        y1 = e.getY();
                        // Redessiner uniquement la forme complexe sans réafficher les sous-formes
                        paint(drawingPanel);

                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            	 if (groupShape.size() == 0|| !isPointInsideGroupShape(groupShape, e.getPoint())) {
            	        // Aucune forme dans le groupe
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            	    } else if (isNearBorder(groupShape.get(0), e.getPoint())) {
            	        // La souris est proche du bord de la forme
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            	    } else {
            	        // La souris est à l'intérieur de la forme mais pas proche du bord
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            	    }
            }

        });

        newButtonDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMode = newButtonDel.isSelected();
                selectMode = false;
                addMode = false;
                movingShape = false;
                if (deleteMode) {
                    shapes.removeAll(groupShape);
                    groupShape.clear();
                }

                selectStartX = selectStartY = selectEndX = selectEndY = 0;
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
                movingShape = false;
                groupShape.clear();
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
                firstClickDone=false;
                selectMode = false;
                movingShape = false;
                groupShape.clear();
                paint(drawingPanel);

            }
        });

        differenceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOperation(OperationType.DIFFERENCE,drawingPanel);
                
            }
        });

        unionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	performOperation(OperationType.UNION,drawingPanel);
            }
        });
        newButtonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serializeShape();
            }
        });

        intersectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	performOperation(OperationType.INTERSECTION,drawingPanel);
            }
        });
        buttonUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo=buttonUndo.isSelected();
               
                performOperation(null,drawingPanel);
                paint(drawingPanel);

            }
        });

    }
    
    private void selectShapesInArea() {
        groupShape.clear();
        int[] rectangleParams = {Math.min(selectStartX, selectEndX), Math.min(selectStartY, selectEndY),
                Math.abs(selectEndX - selectStartX), Math.abs(selectEndY - selectStartY)};
        GeometricShapes geometricShapes = new GeometricShapes();
        GeometricShapes.Rectangle selectionRect = geometricShapes.new Rectangle(rectangleParams);


        for (Shape shape : shapes) {
        	if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                if (isComplexShapeInArea(complexShape,selectionRect)) groupShape.add(complexShape);
        	}
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                if (isCircleInArea(circle)) groupShape.add(circle);
                
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                if (rectangle.getBounds().intersects(selectionRect.getBounds())) 
                    groupShape.add(rectangle);
                
            }
        }
        System.out.println("Number of selected shapes: " + groupShape.size());
    }
    

    private boolean isCircleInArea(Circle circle) {
        int centerX = circle.getX();
        int centerY = circle.getY();
        int radius = circle.getWidth();

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
    
    private boolean isComplexShapeInArea(ComplexShape complexShape, GeometricShapes.Rectangle selectionRect) {
    	 System.out.println("Checking if complex shape is selected");
    	    System.out.println("Mouse click coordinates: (" + x1 + ", " + y1 + ")");
    	    System.out.println("Complex shape bounds: " + complexShape.getBounds());
    	   for (Shape subShape : complexShape.getShapes()) {
            if (subShape instanceof Circle) {
                Circle circle = (Circle) subShape;
                if (isCircleInArea(circle)) {
                    return true;
                }
            } else if (subShape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) subShape;
                if (rectangle.getBounds().intersects(selectionRect.getBounds())) {
                    return true;
                }
            }
            // Ajoutez d'autres conditions pour les autres types de formes si nécessaire
        }
        return false;
    }
    
    private void performOperation(OperationType operation, JPanel drawingPanel) {
    	System.out.println(groupShape.size()); 
    	if (groupShape.size() == 1 && undo) {
    		          // Si une seule forme est sélectionnée et que l'opération undo est activée
            Shape removedShape = groupShape.get(0);
            // Retirer la forme de la liste des formes actuelles
            shapes.remove(removedShape);
            // Restaurer les formes précédentes depuis la liste UNDOshapes
            shapes.addAll(((ComplexShape) removedShape).getUNDOshapes());
            groupShape.addAll(((ComplexShape) removedShape).getUNDOshapes());
            groupShape.remove(removedShape);
            // Nettoyer la liste des formes UNDO
            ((ComplexShape) removedShape).setUNDOshapes(new ArrayList<>());
        } else if (groupShape.size() != 2 || operation==null) {
            JOptionPane.showMessageDialog(frame, "Select exactly two shapes for the operation or one to undo");
            return;
        } else {

        Shape shape1 = groupShape.get(0);
        Shape shape2 = groupShape.get(1);

        if (shape1 != null && shape2 != null) {
            Area area1 = new Area(shape1.getShape());
            Area area2 = new Area(shape2.getShape());

            switch (operation) {
                case DIFFERENCE:
                    area1.subtract(area2);
                    break;
                case UNION:
                    area1.add(area2);
                    break;
                case INTERSECTION:
                    area1.intersect(area2);
                    break;
            }

            if (!area1.isEmpty()) {
                ArrayList<Shape> complexShapes = new ArrayList<>();
                complexShapes.add(shape1);
                complexShapes.add(shape2);
                  
                GeometricShapes.ComplexShape complexShape = new GeometricShapes().new ComplexShape(area1, complexShapes,operation);
               
                	groupShape.add(complexShape) ;
                	shapes.add(complexShape);
                	groupShape.removeAll(complexShapes) ;
                	
                	complexShape.setUNDOshapes(complexShapes); 
                	shapes.removeAll(complexShapes);
                
                paint(drawingPanel);
            } else {
                JOptionPane.showMessageDialog(frame, "The resulting shape is empty.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "One of the selected shapes is null.");
        }
    }
   }
}
