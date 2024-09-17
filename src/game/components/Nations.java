package game.components;

import java.util.ArrayList;

// Wrapper for a List of Nations
class Nations extends GameElement {
    ArrayList<Nation> nations = new ArrayList<>();

    public Nations(Tiles tiles, int numCivs) {
        for (int i = 0; i < numCivs; i++) {
            Nation nation = new Nation(i);
            Tiles.Tile tile = tiles.getRandomTile();
            while (tile.getType() <= 1 || tile.hasUnit()) {
                tile = tiles.getRandomTile();
            }

            Unit initalSettler = new Unit(nation, 0, tile);

            nation.addUnit(initalSettler);
            nations.add(nation);
        }
    }

    ;

    public Nations(ArrayList<Nation> nations) {
        this.nations = nations;
    }

    ;

    public Nation getNation(int index) {
        return nations.get(index);
    }

    ;

    public ArrayList<Nation> getNations() {
        return nations;
    }

    ;

    public void nextTurn() {
        for (Nation nation : nations) {
            nation.nextTurn();


        }
    }
}
