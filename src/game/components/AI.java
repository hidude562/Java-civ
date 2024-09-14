package game.components;

class AI {
    private Nation nation;
    private World world;

    public AI(Nation nation, World world) {
        this.nation = nation;
        this.world = world;
    }

    public void takeTurn() {
        // Manage cities
        for (City city : nation.getCities().getCities()) {
            manageCity(city);
        }

        // Manage units
        for (int i = 0; i < nation.getUnits().size(); i++) {
            Nation.Unit u = nation.getUnits().getUnit(i);
            manageUnit(u);
        }
    }

    private void manageCity(City city) {
        city.autoAssignWorkers();
        if (!city.isBuildingSomething()) {
            city.setProduction(nation.new Unit((int) (Math.random() * 2)));
        }
    }

    private void manageUnit(Nation.Unit unit) {
        if (unit.getType() == 0) { // Settler
            handleSettler(unit);
        } else if (unit.getType() == 1) { // Warrior
            handleWarrior(unit);
        }
    }

    private void handleSettler(Nation.Unit settler) {
        // Currently finds empty spot in a kinda 3 tile up down left right direction
        if (settler.pathIsEmpty()) {
            if (settler.getTile().canBuildCityHere()) {
                settler.unitAction(0);
            } else {
                for (int i = 0; i < 4; i++) {

                    Tiles.Tile citySpot = settler.getTile().getTileFromRelativeXY(
                            new Vert2D((i % 2) * (i / 2 * 2 - 1) * 3, ((i + 1) % 2) * (i / 2 * 2 - 1) * 3)
                    );
                    if (citySpot != null && citySpot.canBuildCityHere()) {
                        if (settler.setPath(
                                citySpot
                        )) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void handleWarrior(Nation.Unit warrior) {
        if (warrior.pathIsEmpty()) {
            // Find the nearest enemy unit or city
            Tiles.Tile target = findNearestEnemyOrCity(warrior);
            if (target != null) {
                warrior.setPath(target);
            } else {
                if (warrior.pathIsEmpty()) {
                    warrior.setPath(world.getTiles().getRandomTile());
                }
            }
        }
    }

    private Tiles.Tile findBestSettleLocation(Nation.Unit settler) {
        // This is a very basic implementation. You might want to make this smarter.
        Tiles.Tile currentTile = settler.getTile();
        Tiles.Tile[] neighbors = currentTile.getNeighborTiles();

        for (Tiles.Tile tile : neighbors) {
            if (tile != null && isGoodSettleLocation(tile)) {
                return tile;
            }
        }

        return null;
    }

    private boolean isGoodSettleLocation(Tiles.Tile tile) {
        // This is a very basic check. You might want to make this smarter.
        return tile.getType() == 4 || tile.getType() == 5; // Grassland or Plains
    }

    private Tiles.Tile findNearestEnemyOrCity(Nation.Unit unit) {
        /*
        // This is a very basic implementation. You might want to implement a proper search algorithm.
        Tiles.Tile currentTile = unit.getTile();
        Tiles.Tile[] neighbors = currentTile.getNeighborTiles();

        for (Tiles.Tile tile : neighbors) {
            if(tile != null) {
                if ((tile.hasUnit() && tile.getUnit(0).getNation() != nation) ||
                        (tile.hasCityCenter() && tile.getCityCenter().getNation() != nation)) {
                    return tile;
                }
            }
        }
        */
        return null;
    }
}
