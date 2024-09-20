package game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

class SwingMain extends JFrame {
    private World world;
    private GamePanel gamePanel;
    private JButton nextTurnButton;
    private JLabel turnLabel;
    private int turnCount;
    private Map<Nation, Color> nationColors;

    public SwingMain() {
        world = new World(new Vert2D(32, 20));
        turnCount = 0;
        initializeNationColors();

        setTitle("NATIONS");
        setSize(1200, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        nextTurnButton = new JButton("Next Turn");
        turnLabel = new JLabel("Turn: 0");

        add(gamePanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(nextTurnButton);
        bottomPanel.add(turnLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        nextTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextTurn();
            }
        });

        updateDisplay();
    }

    private void initializeNationColors() {
        nationColors = new HashMap<>();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.DARK_GRAY, Color.ORANGE, Color.WHITE};
        int colorIndex = 0;
        for (Nation nation : world.getNations().getNations()) {
            nationColors.put(nation, colors[colorIndex % colors.length]);
            colorIndex++;
        }
    }

    private void nextTurn() {
        world.nextTurn();
        turnCount++;
        updateDisplay();
    }

    private void updateDisplay() {
        gamePanel.repaint();
        turnLabel.setText("Turn: " + turnCount);
    }

    class GamePanel extends JPanel {
        private static final int TILE_SIZE = 32;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            Vert2D mapSize = world.getTiles().mapSize;
            for (int y = 0; y < mapSize.getY(); y++) {
                for (int x = 0; x < mapSize.getX(); x++) {
                    Tiles.Tile tile = world.getTiles().getTile(world.getTiles().getIndexFromPoint(new Vert2D(x, y)));
                    drawTile(g2d, tile, x * TILE_SIZE, y * TILE_SIZE);
                }
            }
        }

        private void drawTile(Graphics2D g2d, Tiles.Tile tile, int x, int y) {
            // Draw tile background
            g2d.setColor(getTileColor(tile.getType()));
            g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

            // Draw nation border for owned tiles
            if (tile.hasOwnedCity()) {
                Color nationColor = nationColors.get(tile.getOwnedCity().getNation());
                g2d.setColor(nationColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                g2d.setStroke(new BasicStroke(1));
            }

            // Draw tile border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);

            // Draw city
            if (tile.hasCityCenter()) {
                Color cityColor = nationColors.get(tile.getCityCenter().getNation());
                g2d.setColor(cityColor);
                g2d.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            }

            // Draw unit
            if (tile.hasUnit()) {
                Unit unit = tile.getUnit(0);
                Color unitColor = nationColors.get(unit.getNation());
                g2d.setColor(unitColor);
                g2d.fillOval(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            }

            // Draw worked tile
            if (tile.getWorked()) {
                g2d.setColor(Color.YELLOW);
                g2d.drawOval(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);
            }
        }

        private Color getTileColor(int type) {
            switch (type) {
                case 0: return Color.BLUE.darker();  // Water
                case 1: return Color.BLUE;  // Water
                case 2: return Color.WHITE;  // Ice
                case 3: return Color.GREEN.brighter(); // Tundra
                case 4: return Color.GREEN.darker(); // Grassland
                case 5: return Color.YELLOW.darker(); // Plains
                case 6: return Color.ORANGE.darker(); // Desert
                default: return Color.WHITE;
            }
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