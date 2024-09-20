package game.components;

import java.util.ArrayList;

public class World extends GameElement {
    // Nations of the world
    private Nations nations;

    // Tiles
    private Tiles tiles;

    private int turn = 0;

    private ArrayList<AI> ais = new ArrayList<>();

    public World(Vert2D size) {
        tiles = new Tiles(size);
        tiles.newWorld();
        nations = new Nations(tiles, 5);

        for (int i = 0; i < nations.getNations().size(); i++) {
            ais.add(new AI(nations.getNation(i), this));
        }
    }

    public Nations getNations() {
        return nations;
    }

    public Tiles getTiles() {
        return tiles;
    }

    public int getTurn() {
        return turn;
    }

    public void nextTurn() {
        turn++;
        nations.nextTurn();

        // Let AIs take their turns
        for (AI ai : ais) {
            ai.takeTurn();
        }
    }

    public String toString() {
        return tiles.toString();
    }
}
