package forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import forms.GeometricShapes.Circle;
import forms.GeometricShapes.ComplexShape;
import forms.GeometricShapes.Rectangle;
import forms.GeometricShapes.Shape;


public class App extends JFrame {
	private static final long serialVersionUID = 1L;
	ArrayList<Shape> shapes;
    ArrayList<Shape> selectionShapes;
   
    private JFrame frame;
    private String selectedShape;
    private int x1, y1, x2, y2, dx, dy;
    private int selectStartX, selectStartY, selectEndX, selectEndY;
    private int threshold=20; //distance of resize detection from border
    private boolean firstClickDone = false;
    private boolean deleteMode = false;
    private boolean selectMode = false;
    private boolean addMode = false;
    private boolean resizeMode = false;
    private boolean movingShape = false; 
    private boolean undo=false;
	private boolean saveMode=false;
	private boolean saveRMIMode = false;
    private File lastUsedFile;
    
    public enum OperationType {
        DIFFERENCE,
        UNION,
        INTERSECTION
    }
    
    public static void main(String[] args) {
    	if (args.length > 0 && args[0].equals("-s")) { //Serveur RMI
            Serveurrmi.main(new String[0]);
        } else if (args.length > 0 && args[0].equals("-t")) { //TerminalDraw
            TerminalDraw.main(new String[0]);
        }
    	else {
        	EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new App();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    
    //Serialization
    public void serializeShape() {
        JFileChooser fileChooser = new JFileChooser();       
        
        if (lastUsedFile != null && saveMode) { //SAVE
            File file  = lastUsedFile;
            JOptionPane.showMessageDialog(frame, "The file has beeen saved");
            saveShapesToFile(file);
            return;
            
        } else if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { //SAVE AS   
        	
        	File file = fileChooser.getSelectedFile();                  
            if (file.exists()) {
                int result = JOptionPane.showConfirmDialog(null, "Replace this file?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            lastUsedFile=file;
            saveShapesToFile(file);
        }
    }

    private void saveShapesToFile(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(shapes);   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeShape() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            lastUsedFile=file;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = in.readObject();
                if (obj instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
					ArrayList<Shape> serializedShapes = (ArrayList<Shape>) obj;
                    for (Shape serializedShape : serializedShapes) {
                        
                    	if (serializedShape instanceof ComplexShape) { // reconstruction with subShapes                            
                            ComplexShape complexShape = (ComplexShape) serializedShape;
                            complexShape.applyOperation(); 
                            shapes.add(complexShape);
                            
                        } else { // simple shapes                
                            shapes.add(serializedShape);
                        }
                    }
                }              
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } 
    }
    
    //Other operations
    public void resetCoordinates() {
        x1 = y1 = x2 = y2 = dx = dy = 0;
    }

    @SuppressWarnings("exports")
	public boolean isNearBorder(GeometricShapes.Shape shape, Point clickPoint) {   
        return clickPoint.x <= shape.getX() + threshold || 
               clickPoint.x >= shape.getX() + shape.getWidth() - threshold ||
               clickPoint.y <= shape.getY() + threshold || 
               clickPoint.y >= shape.getY() + shape.getHeight() - threshold;
    }
    
    @SuppressWarnings("exports")
	public boolean isPointInsideselectionShapes(ArrayList<GeometricShapes.Shape> selectionShapes, Point point) {
		if (selectionShapes.isEmpty()) {
			return false;
		}
		GeometricShapes.Shape selectedShape = selectionShapes.get(0); //TODO check any shape (good enough for now)
		
		return point.x >= selectedShape.getX() && point.x <= selectedShape.getX() + selectedShape.getWidth() &&
				point.y >= selectedShape.getY() && point.y <= selectedShape.getY() + selectedShape.getHeight();
	}
    
    private boolean isInsideAnyShape(Point point) {
        for (Shape shape : shapes) {
            if (shape.contains(point)) return true;
        }
        return false;
    }
        
    private boolean isComplexShapeSelected(ComplexShape complexShape, Point point) {
    	for (Shape subShape : complexShape.getShapes()) {
    		if (subShape.contains(point)) return true;
    	}
    	return false;
    }
    
    private void selectShapesInArea() {
        selectionShapes.clear();
        int[] rectangleParams = {Math.min(selectStartX, selectEndX), Math.min(selectStartY, selectEndY),
                Math.abs(selectEndX - selectStartX), Math.abs(selectEndY - selectStartY)};
        GeometricShapes geometricShapes = new GeometricShapes();
        GeometricShapes.Rectangle selectionRect = geometricShapes.new Rectangle(rectangleParams);

        for (Shape shape : shapes) {
        	if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                if (isComplexShapeInArea(complexShape,selectionRect)) selectionShapes.add(complexShape);
        	}
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                if (isCircleInArea(circle)) selectionShapes.add(circle);
                
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                if (rectangle.getBounds().intersects(selectionRect.getBounds())) 
                    selectionShapes.add(rectangle);
                
            }
        }
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
        }
        return false;
    }
    
    private void performOperation(OperationType operation, JPanel drawingPanel) {
    	if (selectionShapes.size() == 1 && undo) {
    		          
            Shape removedShape = selectionShapes.get(0);
            shapes.remove(removedShape);
            shapes.addAll(((ComplexShape) removedShape).getUNDOshapes());
            selectionShapes.addAll(((ComplexShape) removedShape).getUNDOshapes());
            selectionShapes.remove(removedShape);
            ((ComplexShape) removedShape).setUNDOshapes(new ArrayList<>());
            
        } else if (selectionShapes.size() != 2 || operation==null) {
            JOptionPane.showMessageDialog(frame, "Select exactly one shape to undo or two shapes for the operation");
            return;
        } else {
        	Shape shape1 = selectionShapes.get(0);
        	Shape shape2 = selectionShapes.get(1);

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
               
                	selectionShapes.add(complexShape) ;
                	shapes.add(complexShape);
                	selectionShapes.removeAll(complexShapes) ;
                	
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
    
    @SuppressWarnings("exports")
	public void paint(JPanel drawingPanel) {
        Graphics g = drawingPanel.getGraphics();
        if (g == null) {
            return; 
        }
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());

        for (Shape shape : shapes) {
            if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                complexShape.draw((Graphics2D) g); 
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
            g.setColor(new Color(0, 0, 255, 100)); //blue transparant
            int width = Math.abs(selectEndX - selectStartX);
            int height = Math.abs(selectEndY - selectStartY);
            int x = Math.min(selectStartX, selectEndX);
            int y = Math.min(selectStartY, selectEndY);
            g.fillRect(x, y, width, height);           
        }
        
        for (Shape shape : selectionShapes) {
            if (shape instanceof ComplexShape) {
                ComplexShape complexShape = (ComplexShape) shape;
                complexShape.drawBorder((Graphics2D) g,drawingPanel); //also draw borders of subShapes
            } else if(shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                rectangle.paintWithSelectionBorder(drawingPanel);
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                circle.paintWithSelectionBorder(drawingPanel);
            }
        }
    }

    public App() {
        shapes = new ArrayList<>();    
        selectionShapes = new ArrayList<>();   
        selectedShape = "Rectangle";
        initialize();      
    }
    
    public void initialize() {
    	
        frame = new JFrame("ManipShape");
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

        JToggleButton newButtonSelect = new JToggleButton("<html><b>SELECT</b></html>"); //bold
        JToggleButton newButtonAdd = new JToggleButton("<html><b>ADD</b></html>");
        JToggleButton newButtonDel = new JToggleButton("<html><b>DELETE</b></html>");   
        JToggleButton buttonUndo = new JToggleButton("<html><b>UNDO</b></html>");
        toolBar.add(newButtonSelect);
        toolBar.add(newButtonAdd);
        toolBar.add(newButtonDel);
        toolBar.add(buttonUndo);
        
        toolBar.addSeparator();
        JButton differenceButton = new JButton("<html><b>DIFFERENCE</b></html>");
        JButton unionButton = new JButton("<html><b>UNION</b></html>");
        JButton intersectionButton = new JButton("<html><b>INTERSECTION</b></html>");
        toolBar.add(differenceButton);
        toolBar.add(unionButton);
        toolBar.add(intersectionButton);
        
        toolBar.addSeparator();
        JComboBox<String> shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Circle","prefab1","prefab2","prefab3"});
        toolBar.add(shapeComboBox);

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
        
        toolBar.addSeparator();
        JComboBox<String> saveComboBox = new JComboBox<>(new String[]{"Save", "Save as","RMI Save","RMI Load"});
        toolBar.add(saveComboBox);
        saveComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	saveMode=((String) saveComboBox.getSelectedItem()=="Save")?true:false;
            	saveRMIMode=((String) saveComboBox.getSelectedItem()=="RMI Save")?true:false;
                if (saveComboBox.getSelectedItem()!="RMI Save" && saveComboBox.getSelectedItem()!="RMI Load") serializeShape();
            }
        });
        
        for (Component component : toolBar.getComponents()) { // TODO hover on JComboBox doesn't work
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
        
        JButton rmiSaveLoadButton = new JButton("RMI Save/Load"); //Default is load
        rmiSaveLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = JOptionPane.showInputDialog(frame, "Enter RMI Server IP:", "RMI Server IP", JOptionPane.PLAIN_MESSAGE);
                if (serverIP != null && !serverIP.isEmpty()) {
                        RemoteShapeService remoteService = null;
						try {
							remoteService = (RemoteShapeService) Naming.lookup("//" + serverIP + ":1099/RemoteShapeService");
						} catch (MalformedURLException | RemoteException | NotBoundException e1) {						
							 JOptionPane.showMessageDialog(frame, "Server doesn't exist");
							 return;
						}
                        if (saveRMIMode) {  //Save on RMI server                     
                            try {
								remoteService.saveShapes(shapes);
		
							} catch (RemoteException e1) {
								JOptionPane.showMessageDialog(frame, "Save failed");
								return;
							}
                            JOptionPane.showMessageDialog(frame, "Shapes saved on RMI server.");
                        } else {  //Load from RMI server
                        	try {
                        	    selectionShapes.clear();
                        	    ArrayList<Shape> loadedShapes = (ArrayList<Shape>) remoteService.loadShapes();
                        	    ArrayList<Shape> recombinatedShapes = new ArrayList<>();

                        	    for (Shape s : loadedShapes) {
                        	        if (s instanceof ComplexShape) { // Reconstruction with subShapes 
                        	            ComplexShape complexShape = (ComplexShape) s;
                        	            ArrayList<Shape> subShapes = new ArrayList<>();
                        	            
                        	            for (Shape subShape : complexShape.getShapes()) {
                        	                if (subShape instanceof GeometricShapes.Rectangle) {
                        	                    subShapes.add(subShape);
                        	                } else if (subShape instanceof GeometricShapes.Circle) {
                        	                	subShapes.add(subShape);
                        	                } else if (subShape instanceof GeometricShapes.ComplexShape) {
                        	                	subShapes.add(subShape);
                        	                }
                        	            }
                        	            
                        	            ComplexShape newComplexShape = new GeometricShapes().new ComplexShape((Area) complexShape.getShape(), subShapes, complexShape.getOperation());
                        	            newComplexShape.applyOperation();
                        	            recombinatedShapes.add(newComplexShape);
                        	        } else {
                        	            recombinatedShapes.add(s);
                        	        }
                        	    }
                        	    shapes = recombinatedShapes;
                        	    JOptionPane.showMessageDialog(frame, "Shapes loaded from RMI server.");
                        	    paint(drawingPanel);
                        	} catch (RemoteException e1) {
                        		JOptionPane.showMessageDialog(frame, "Load failed");
								return;
                        	}
                        }
                }
            }
        });
        toolBar.add(rmiSaveLoadButton);
        
        deserializeShape();
        paint(drawingPanel);
        
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
                                    selectionShapes.add(complexShape);
                                    paint(drawingPanel);
                                    return;
                                }
                            } else {
                                selectionShapes.add(shape);
                                paint(drawingPanel);
                                return;
                            }
                        }
                    }
                } else if (movingShape) {
                    for (Shape shape : selectionShapes) {
                        if (shape.contains(e.getPoint())) {
                            shape.setBounds(dx, dy);
                            movingShape = false;
                            selectionShapes.clear();
                            paint(drawingPanel);
                            return;
                        }
                    }
                } else { //Unselect
                    if (!isInsideAnyShape(e.getPoint())) {
                        selectionShapes.clear(); 
                        paint(drawingPanel);
                    }
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (selectMode) {
                    resizeMode = true; //by default on
                    paint(drawingPanel);
                    selectStartX = e.getX();
                    selectStartY = e.getY();
                    selectEndX = e.getX();
                    selectEndY = e.getY();
                } else {
                    if (deleteMode) {
                        for (int i = shapes.size() - 1; i >= 0; i--) {
                            if (shapes.get(i).contains(e.getPoint())) {
                                shapes.remove(i);
                                paint(drawingPanel);
                                return;
                            }
                        }
                    }

                    if (addMode) {
                        paint(drawingPanel);
                        if (selectedShape.equals("Circle")||selectedShape.equals("Rectangle")) {
                            if (!firstClickDone) {
                                x1 = e.getX(); //1st click
                                y1 = e.getY();
                                firstClickDone = true;
                            }
                        } else {
                        	x1 = e.getX();
                            y1 = e.getY();
                        	if (selectedShape.equals("prefab1")){// TODO switch + visualisation in other JPanel + save as prefab
                        		int width =150;
                        		int height =150;
                        		int[] rectangleParams = {x1,y1, width, height};
                        		GeometricShapes.Rectangle newRectangle = new GeometricShapes().new Rectangle(rectangleParams);
                            
                        		int radius = 45;
                        		int[] circleParams = {x1 - radius, y1 - radius, radius * 2};
                        		GeometricShapes.Circle newCircle =new GeometricShapes().new Circle(circleParams);
                            
                        		selectionShapes.clear();
                        		selectionShapes.add(newCircle);
                        		selectionShapes.add(newRectangle);
                        		performOperation(OperationType.DIFFERENCE,drawingPanel);
                        	
                        	} else if (selectedShape.equals("prefab2")) {
                        		int radius = 45;
                        		int[] circleParamsA = {x1 - 2*radius, y1 - radius, radius * 2};
                        		GeometricShapes.Circle newCircleA =new GeometricShapes().new Circle(circleParamsA);
                            
                        		int[] circleParamsB = {x1 - radius, y1 - radius, radius * 2};
                        		GeometricShapes.Circle newCircleB =new GeometricShapes().new Circle(circleParamsB);
                            
                        		selectionShapes.clear();
                        		selectionShapes.add(newCircleA);
                        		selectionShapes.add(newCircleB);
                        		performOperation(OperationType.INTERSECTION,drawingPanel);
                        	}else if (selectedShape.equals("prefab3")) {
                        		int width =30;
                        		int height =150;
                        		int[] rectangleParamsA = {x1-height,y1, width, height};
                        		GeometricShapes.Rectangle newRectA = new GeometricShapes().new Rectangle(rectangleParamsA);
                            
                        		int[] rectangleParamsB = {x1+height,y1, width, height};
                        		GeometricShapes.Rectangle newRectB = new GeometricShapes().new Rectangle(rectangleParamsB);
                            
                        		selectionShapes.clear();
                        		selectionShapes.add(newRectA);
                        		selectionShapes.add(newRectB);
                        		performOperation(OperationType.UNION,drawingPanel);
                        		
                        	}
                        	firstClickDone = true;
                    		paint(drawingPanel);
                        }
                    } else if (selectionShapes.size() > 0) {
                    	boolean clickedInsideShape = false;
                        for (Shape shape : selectionShapes) {
                            if (shape.contains(e.getPoint())) {
                                clickedInsideShape = true;
                                break;
                            }
                        }
                        if (clickedInsideShape) {
                            x1 = e.getX(); //1st click
                            y1 = e.getY();
                            if (selectionShapes.size() == 0 || !isPointInsideselectionShapes(selectionShapes, e.getPoint())) {
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    	    } else if (isNearBorder(selectionShapes.get(0), e.getPoint())) { //Resize
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    	        resizeMode = true;
                                movingShape = false;
                    	    } else { //Move
                    	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    	        movingShape = true;
                                resizeMode = false;
                    	    }
                        } else {
                            selectionShapes.clear();
                            if (newButtonSelect.isSelected()) selectMode = true;
                            selectStartX = e.getX(); // 1st selection click
                            selectStartY = e.getY();
                            selectEndX = e.getX();
                            selectEndY = e.getY();
                            paint(drawingPanel);
                        }
                    } else {
                        selectMode = true;
                        selectStartX = e.getX(); //1st selection click
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
                        }  else if (movingShape) { //ComplexShape
                            dx = e.getX() - x1;
                            dy = e.getY() - y1;
                            for (Shape shape : selectionShapes) { //Move each shape
                                shape.setBounds(dx, dy);
                            }
                            ((ComplexShape) selectionShapes.get(0)).updateBounds(dx, dy);
                            x1 = e.getX();
                            y1 = e.getY();
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
                    if (selectionShapes.size() == 0|| !isPointInsideselectionShapes(selectionShapes, e.getPoint())) {
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            	    } else if (isNearBorder(selectionShapes.get(0), e.getPoint())) {
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            	    } else {
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            	    }
                    paint(drawingPanel);
                } else {
                    if (!deleteMode && firstClickDone && addMode) {
                        paint(drawingPanel);
                        if (selectedShape.equals("Circle")) {
                            int radius = (int) Math.sqrt(Math.pow(e.getX() - x1, 2) + Math.pow(e.getY() - y1, 2));
                            Graphics g = drawingPanel.getGraphics();
                            g.setColor(Color.GREEN); // Preview of the shape
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
                        
                    } else if (resizeMode) {
                        int dx = e.getX() - x1;
                        int dy = e.getY() - y1;
                        for (Shape selectedShape : selectionShapes) {
                            if (selectedShape instanceof Circle) {
                                Circle circle = (Circle) selectedShape;
                                circle.resize(dx, dy);
                            } else if (selectedShape instanceof Rectangle) {
                                Rectangle rectangle = (Rectangle) selectedShape;
                                rectangle.resize(dx, dy);
                            } else if (selectedShape instanceof ComplexShape) { //TODO resize each selectedShape not only 1st
                            	 ((ComplexShape) selectionShapes.get(0)).resize(dx, dy);
                            }
                            
                        }
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                        
                    } else if (movingShape) {
                        int dx = e.getX() - x1;
                        int dy = e.getY() - y1;
                        for (Shape shape : selectionShapes) {
                            shape.setBounds(dx,dy);
                        }
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                    
                    } else if (!addMode){
                        ((ComplexShape) selectionShapes.get(0)).updateBounds(dx, dy);
                        x1 = e.getX();
                        y1 = e.getY();
                        paint(drawingPanel);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            	 if (selectionShapes.size() == 0|| !isPointInsideselectionShapes(selectionShapes, e.getPoint())) {
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            	    } else if (isNearBorder(selectionShapes.get(0), e.getPoint())) {
            	        drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            	    } else {
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
                    shapes.removeAll(selectionShapes);
                    selectionShapes.clear();
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
                selectionShapes.clear();
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
                selectionShapes.clear();
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
}
