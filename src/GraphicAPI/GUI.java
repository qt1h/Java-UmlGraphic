package GraphicAPI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI extends JFrame {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI window = new GUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setFloatable(false);
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        // Ajouter des composants flexibles à gauche et à droite de la JToolBar
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

        // Menu déroulant des formes
        JComboBox<String> shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Circle", "Triangle"});
        toolBar.add(shapeComboBox);

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedShape = (String) shapeComboBox.getSelectedItem();
                System.out.println("Selected Shape: " + selectedShape);
            }
        });

        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
        toolBar.setBorder(BorderFactory.createCompoundBorder(toolBar.getBorder(), bottomBorder));

        // Zone de dessin (exemple)
        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE); // couleur de fond pour la zone de dessin
        frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);
    }
}
