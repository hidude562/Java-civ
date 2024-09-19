package game.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class AI {
    private Nation nation;
    private World world;

    public AI(Nation nation, World world) {
        this.nation = nation;
        this.world = world;
    }

    public void takeTurn() {
        manageCities();
        manageUnits();
        manageDiplomacy();
    }

    private void manageCities() {
        for (City city : nation.getCities().getCities()) {
            manageCity(city);
        }
    }

    private void manageUnits() {
        List<Unit> units = nation.getUnits().getUnits();
        units.sort(Comparator.comparingInt(u -> u.getType().getReference()));

        for (Unit unit : units) {
            manageUnit(unit);
        }
    }

    private void manageCity(City city) {
        city.autoAssignWorkers();
        if (!city.isBuildingSomething()) {
            if (shouldBuildUnit(city)) {
                buildUnit(city);
            } else {
                buildBuilding(city);
            }
        }
    }

    private boolean shouldBuildUnit(City city) {
        int cityCount = nation.getCities().getCities().size();
        int unitCount = nation.getUnits().size();
        return unitCount < cityCount * 3 || Math.random() < 0.3;
    }

    private void buildUnit(City city) {
        List<GameThings.UnitConfigReference> unitsUnlocked = nation.getTechTree().getUnlocks().getUnitsRefs();
        if (!unitsUnlocked.isEmpty()) {
            GameThings.UnitConfigReference unitToBuild = selectBestUnit(unitsUnlocked);
            city.setProduction(unitToBuild);
        }
    }

    private GameThings.UnitConfigReference selectBestUnit(List<GameThings.UnitConfigReference> units) {
        // Implement logic to choose the best unit based on current needs
        return units.get((int) (Math.random() * units.size()));
    }

    private void buildBuilding(City city) {
        List<GameThings.BuildingConfigReference> buildingsCanMake = city.getBuildingsCanMake();
        if (!buildingsCanMake.isEmpty()) {
            GameThings.BuildingConfigReference buildingToBuild = selectBestBuilding(buildingsCanMake);
            city.setProduction(buildingToBuild);
        }
    }

    private GameThings.BuildingConfigReference selectBestBuilding(List<GameThings.BuildingConfigReference> buildings) {
        // Implement logic to choose the best building based on current needs
        return buildings.get((int) (Math.random() * buildings.size()));
    }

    private void manageUnit(Unit unit) {
        switch (unit.getType().getReference() % 3) {
            case 0:
                handleSettler(unit);
                break;
            case 1:
                handleDefensiveUnit(unit);
                break;
            case 2:
                handleOffensiveUnit(unit);
                break;
        }
    }

    private void handleSettler(Unit settler) {
        if (settler.pathIsEmpty()) {
            Tiles.Tile bestLocation = findBestSettleLocation(settler);
            if (bestLocation != null) {
                settler.setPath(bestLocation);
            } else if (settler.getTile().canBuildCityHere()) {
                settler.unitAction(0);
            }
        }
    }

    private void handleDefensiveUnit(Unit unit) {
        if (unit.pathIsEmpty()) {
            City cityToDefend = findMostThreatenedCity();
            if (cityToDefend != null) {
                unit.setPath(cityToDefend.getCityCenterTile());
            } else {
                patrolBorders(unit);
            }
        }
    }

    private void handleOffensiveUnit(Unit unit) {
        if (unit.pathIsEmpty()) {
            Tiles.Tile target = findBestAttackTarget(unit);
            if (target != null) {
                unit.setPath(target);
            } else {
                exploreTerritory(unit);
            }
        }
    }

    private Tiles.Tile findBestSettleLocation(Unit settler) {
        // Implement a more sophisticated algorithm to find the best settle location
        // Consider factors like resources, distance from other cities, terrain, etc.
        Tiles.Tile[] tiles = settler.getTile().getTilesInRange(4);
        for(Tiles.Tile tile : tiles) {
            if(tile.canBuildCityHere()) {
                return 
            }
        }
        return null;
    }

    private City findMostThreatenedCity() {
        // Implement logic to determine which city is under the most threat
        return null;
    }

    private void patrolBorders(Unit unit) {
        // Implement logic for defensive units to patrol the nation's borders
        Tiles.Tile[] tiles = unit.getTile().getTilesInRange(4);
        for(Tiles.Tile tile : tiles) {
            if(tile.getNationality() == this.nation && tile.isBorderEdge() && tile != unit.getTile() && unit.setPath(tile)) {
                break;
            }
        }
    }

    private Tiles.Tile findBestAttackTarget(Unit unit) {
        // Implement a more sophisticated algorithm to find the best attack target
        // Consider factors like enemy strength, strategic value, etc.
        return null;
    }

    private void exploreTerritory(Unit unit) {
        // Implement logic for units to explore unexplored territory
    }

    private void manageDiplomacy() {
        // Implement basic diplomacy logic
        // For example, propose trade deals, form alliances, or declare war based on AI's goals
    }
}