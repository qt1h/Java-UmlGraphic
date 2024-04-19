package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

import GraphicAPI.GeometricShapes.Cercle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GUI extends JFrame {
    ArrayList<Cercle> cercles;
    private JFrame frame;
    private String selectedShape;
    private int startX, startY; // Coordonnées du premier point

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
    }

    public GUI() {
        initialize();
    }

    public void initialize() {
        cercles = new ArrayList<>();
        selectedShape = "Rectangle";
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
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX(); // Enregistrer les coordonnées du premier point
                startY = e.getY();
                System.out.println(startX);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedShape.equals("Circle")) {
                    int radius = (int) Math.sqrt(Math.pow(e.getX() - startX, 2) + Math.pow(e.getY() - startY, 2));
                    int x = startX - radius; // Coordonnée x du coin supérieur gauche
                    int y = startY - radius; // Coordonnée y du coin supérieur gauche
                    int[] a = {x, y, radius * 2}; // Créez le cercle avec le coin supérieur gauche et le diamètre
                    GeometricShapes geometricShapes = new GeometricShapes();
                    Cercle nouveauCercle = geometricShapes.new Cercle(a);
                    cercles.add(nouveauCercle); // Ajouter le nouveau cercle à la liste
                    paint(drawingPanel); // Redessiner le panneau de dessin
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        drawingPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape.equals("Circle")) {
                    // Récupérer les dimensions du panneau de dessin
                    int panelWidth = drawingPanel.getWidth();
                    int panelHeight = drawingPanel.getHeight();

                    // Dessiner un fond blanc pour effacer les aperçus précédents et les cercles précédemment dessinés
                    Graphics g = drawingPanel.getGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, panelWidth, panelHeight);

                    // Redessiner les cercles précédemment dessinés
                    paint(drawingPanel);

                    // Calculer le rayon du cercle en fonction de la position actuelle de la souris
                    int radius = (int) Math.sqrt(Math.pow(e.getX() - startX, 2) + Math.pow(e.getY() - startY, 2));

                    // Mettre à jour les coordonnées du coin supérieur gauche en fonction de la position actuelle de la souris
                    int x = startX - radius;
                    int y = startY - radius;

                    // Redéfinir les coordonnées et le rayon du cercle
                    int[] a = {x, y, radius * 2};

                    // Dessiner le cercle prévisualisé en vert
                    g.setColor(Color.GREEN);
                    Graphics2D g2d = (Graphics2D) drawingPanel.getGraphics(); // Obtenir le contexte graphique du JPanel
                    Stroke stroke2 = new BasicStroke(8f);
                    g2d.setStroke(stroke2);
                    g2d.drawOval(x, y, radius * 2, radius * 2);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
        });


    }
}
