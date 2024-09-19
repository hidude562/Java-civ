package game.components;

import java.util.ArrayList;

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
            Unit u = nation.getUnits().getUnit(i);
            manageUnit(u);
        }
    }

    private void manageCity(City city) {
        city.autoAssignWorkers();
        if (!city.isBuildingSomething()) {
            boolean buildBuilding = true;
            if(buildBuilding) {
                ArrayList<GameThings.BuildingConfigReference> buildingsCanMake = city.getBuildingsCanMake();
                if(buildingsCanMake.size()>0)
                    city.setProduction(buildingsCanMake.get(
                            (int) (Math.random() * buildingsCanMake.size())
                    ));
            } else {
                ArrayList<GameThings.UnitConfigReference> unitsUnlocked = nation.getTechTree().getUnlocks().getUnitsRefs();
                city.setProduction(unitsUnlocked.get(
                        (int) (Math.random() * unitsUnlocked.size())
                ));
            }
        }
    }

    private void manageUnit(Unit unit) {
        if (unit.getType().getReference() == 0) { // Settler
            handleSettler(unit);
        } else if (unit.getType().getReference() % 3 == 1) { // Defense main unit
            handleDefensiveUnit(unit);
        } else if (unit.getType().getReference() % 3 == 2) { // Attack main unit
            handleOffensiveUnit(unit);
        } else if (unit.getType().getReference() % 3 == 0) { // Attack main unit
            handleExpensiveUnit(unit);
        }
    }

    private void handleDefensiveUnit(Unit unit) {

    }

    private void handleOffensiveUnit(Unit unit) {
        Tiles.Tile target = findNearestEnemyOrCity(unit);
        if (target != null) {
            unit.setPath(target);
        } else {
            if (unit.pathIsEmpty()) {
                unit.setPath(world.getTiles().getRandomTile());
            }
        }
    }

    private void handleExpensiveUnit(Unit unit) {

    }

    private void handleSettler(Unit settler) {
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

    private Tiles.Tile findBestSettleLocation(Unit settler) {
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

    private Tiles.Tile findNearestEnemyOrCity(Unit unit) {

        // This is a very basic implementation. You might want to implement a proper search algorithm.
        Tiles.Tile currentTile = unit.getTile();
        Tiles.Tile[] neighbors = currentTile.getNeighborTiles();

        for (Tiles.Tile tile : neighbors) {
            if(tile != null) {
                if ((tile.hasUnit() && tile.getUnit(0).getNation() != nation) ||
                        (tile.hasCityCenter() && tile.getCityCenter().getNationality() != nation)) {
                    return tile;
                }
            }
        }

        return currentTile.getMap().getRandomTile();
    }
}
