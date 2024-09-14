package game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingMain extends JFrame {
    private World world;
    private JTextArea worldDisplay;
    private JButton nextTurnButton;
    private JLabel turnLabel;
    private int turnCount;

    public SwingMain() {
        // Initialize the game world
        world = new World(new Vert2D(32, 32));
        turnCount = 0;

        // Set up the frame
        setTitle("Civilization-like Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        worldDisplay = new JTextArea();
        worldDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        worldDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(worldDisplay);

        nextTurnButton = new JButton("Next Turn");
        turnLabel = new JLabel("Turn: 0");

        // Add components to the frame
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(nextTurnButton);
        bottomPanel.add(turnLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listener to the button
        nextTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextTurn();
            }
        });

        // Initial display
        updateDisplay();
    }

    private void nextTurn() {
        world.nextTurn();
        turnCount++;
        updateDisplay();
    }

    private void updateDisplay() {
        worldDisplay.setText(world.toString());
        turnLabel.setText("Turn: " + turnCount);

        // Check for settler actions and city production
        Nation playerNation = world.getNations().getNation(0);
        Nation.Unit settler = playerNation.getUnits().getUnit(0);

        if (settler.pathIsEmpty() && turnCount == 1) {
            settler.unitAction(0);
            worldDisplay.append("\nSettler founded a city!");
        }

        if (turnCount == 1) {
            City userCity = playerNation.getCities().getCity(0);
            userCity.setProduction(playerNation.new Unit(0));
            worldDisplay.append("\nCity started producing a unit.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SwingMain().setVisible(true);
            }
        });
    }
}