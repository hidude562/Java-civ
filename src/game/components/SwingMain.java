package game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

class SwingMain extends JFrame {
    private World world;
    private GamePanel gamePanel;
    private JButton nextTurnButton;
    private JLabel turnLabel;
    private int turnCount;
    private Map<Nation, Color> nationColors;
    private Nation playerNation;
    private Unit selectedUnit;
    private City selectedCity;

    public SwingMain() {
        world = new World(new Vert2D(30, 20));
        turnCount = 0;
        initializeNationColors();
        playerNation = world.getNations().getNations().get(0); // Set the first nation as the player's nation

        setTitle("NATIONS");
        setSize(1200, 800);
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

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        updateDisplay();
    }

    private void initializeNationColors() {
        nationColors = new HashMap<>();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.DARK_GRAY, Color.ORANGE};
        int colorIndex = 0;
        for (Nation nation : world.getNations().getNations()) {
            nationColors.put(nation, new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            colorIndex++;
        }
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
                g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
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
                String uRepr = "";
                for(Unit u : tile.getUnits().units) {
                    if(!u.getIsDead()) {
                        uRepr += u.getConfig().getName().substring(0, 1);
                    }
                }
                g2d.drawString(uRepr, x + TILE_SIZE / 2 - 2, y + TILE_SIZE / 2 + 4);
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

    private void handleMouseClick(int x, int y) {
        int tileX = x / GamePanel.TILE_SIZE;
        int tileY = y / GamePanel.TILE_SIZE;
        Tiles.Tile clickedTile = world.getTiles().getTile(world.getTiles().getIndexFromPoint(new Vert2D(tileX, tileY)));

        if (clickedTile != null) {
            if (clickedTile.hasUnit() && clickedTile.getUnit(0).getNation() == playerNation) {
                selectedUnit = clickedTile.getUnit(0);
                selectedCity = null;
                showUnitActions();
            } else if (clickedTile.hasCityCenter() && clickedTile.getCityCenter().getNation() == playerNation) {
                selectedCity = clickedTile.getCityCenter();
                selectedUnit = null;
                showCityActions();
            } else if (selectedUnit != null) {
                selectedUnit.setPath(clickedTile);
                selectedUnit = null;
            }
            updateDisplay();
        }
    }

    private void showUnitActions() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem moveItem = new JMenuItem("Move");
        moveItem.addActionListener(e -> {
            // The next click will set the path for the unit
        });
        popup.add(moveItem);


        SpecialMoveConfig[] unitSpecials = selectedUnit.getConfig().getUnitSpecials();
        for (int i = 0; i < unitSpecials.length; i++) {
            SpecialMoveConfig special = unitSpecials[i];
            JMenuItem specialItem = new JMenuItem(special.getName());
            final int id = i;
            specialItem.addActionListener(e -> {
                selectedUnit.unitAction(id);
                selectedUnit = null;
                updateDisplay();
            });
            popup.add(specialItem);
        }
        Vert2D position = selectedUnit.getTile().getPosition();
        popup.show(gamePanel, position.getX() * GamePanel.TILE_SIZE,
                position.getY() * GamePanel.TILE_SIZE);
    }

    private void showCityActions() {
        JPopupMenu popup = new JPopupMenu();
        JMenu productionMenu = new JMenu("Set Production");

        for (GameThings.UnitConfigReference unitRef : selectedCity.getNation().getTechTree().getUnlocks().getUnitsRefs()) {
            JMenuItem unitItem = new JMenuItem(((UnitConfig) unitRef.get()).getName());
            unitItem.addActionListener(e -> {
                selectedCity.setProduction(unitRef);
                selectedCity = null;
                updateDisplay();
            });
            productionMenu.add(unitItem);
        }

        for (GameThings.BuildingConfigReference buildingRef : selectedCity.getBuildingsCanMake()) {
            JMenuItem buildingItem = new JMenuItem(((BuildingConfig) buildingRef.get()).getName());
            buildingItem.addActionListener(e -> {
                selectedCity.setProduction(buildingRef);
                selectedCity = null;
                updateDisplay();
            });
            productionMenu.add(buildingItem);
        }

        popup.add(productionMenu);
        popup.show(gamePanel, selectedCity.getCityCenterTile().getPosition().getX() * GamePanel.TILE_SIZE,
                selectedCity.getCityCenterTile().getPosition().getY() * GamePanel.TILE_SIZE);
    }

    private void nextTurn() {
        for (Nation nation : world.getNations().getNations()) {
            if (nation != playerNation) {
                new AI(nation, world).takeTurn();
            }
        }
        world.nextTurn();
        turnCount++;
        updateDisplay();
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