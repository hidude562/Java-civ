package game.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class AI {
    private Nation nation;
    private World world;
    private ArrayList<City> threatenedCitiesListCache;

    public AI(Nation nation, World world) {
        this.nation = nation;
        this.world = world;
    }

    public void takeTurn() {
        manageCities();
        manageUnits();
        manageDiplomacy();

        threatenedCitiesListCache = null;
    }

    private void manageCities() {
        for (City city : nation.getCities().getCities()) {
            manageCity(city);
        }
    }

    private void manageUnits() {
        List<Unit> units = nation.getUnits().getUnits();
        units.sort(Comparator.comparingInt(u -> u.getType().getReference()));

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
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
        if(findMostThreatenedCities().contains(city)) {
            return true;
        }
        return Math.random() < 0.5;
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
        if((int) (Math.random() * 3) == 0) return units.get(0);
        return units.get((int) (units.size() - Math.random() * 2)); //
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
        int unitType = unit.getType().getReference();
        if(unitType == 0) {
            handleSettler(unit);
            return;
        }
        if(unitType >= 1 && unitType <= 3) {
            handleFamousPerson(unit);
        }
        switch (unitType % 3) {
            case 0:
                handleOffensiveUnit(unit);
                break;
            case 1:
                handleDefensiveUnit(unit);
                break;
            case 2:
                handleOffensiveUnit(unit);
                break;
        }
    }

    private void handleFamousPerson(Unit guy) {
        if (guy.pathIsEmpty()) {
            if(guy.getTile().getCityCenter() == null) {
                // TODO: Handle cases where unit for whatever reason isnt there
            } else {
                guy.unitAction((int) (Math.random() * 2));
            }
        }
    }

    private void handleSettler(Unit settler) {
        if (settler.pathIsEmpty()) {
            if (settler.getTile().canBuildCityHere()) {
                settler.unitAction(0);
            }
            Tiles.Tile bestLocation = findBestSettleLocation(settler);
            if (bestLocation != null) {
                System.out.println(settler.setPath(bestLocation));
            } else {
                // TODO: Selling units?
                settler.setDead();
            }
        }
    }

    private void handleDefensiveUnit(Unit unit) {
        if (unit.pathIsEmpty()) {
            ArrayList<City> citiesToDefend = findMostThreatenedCities();
            if(citiesToDefend.isEmpty()) {return;}

            int lowestDistance = 4;
            City lowestCity = null;

            for(City c : citiesToDefend) {
                int dist = unit.distanceToTile(c.getCityCenterTile());
                if(dist < lowestDistance) {
                    lowestDistance = dist;
                    lowestCity = c;
                }
            }
            if(lowestCity != null) {
                unit.setPath(lowestCity.getCityCenterTile());
            }
            /*
            else {
                patrolBorders(unit);
            }
             */
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

        for(int i = 3; i < 12; i+=3) {
            Tiles.Tile[] tiles = settler.getTile().getTilesExactlyInRange(i);
            for (Tiles.Tile tile : tiles) {
                if (tile != null && tile.canBuildCityHere() && settler.canTravel(tile)) {
                    return tile;
                }
            }
        }
        return null;
    }

    private ArrayList<City> findMostThreatenedCities() {
        if(threatenedCitiesListCache == null) {
            // Implement logic to determine which city is under the most threat
            // TODO: Logic by getting the most menacing tiles
            ArrayList<City> cities = new ArrayList<City>();
            for (City c : nation.getCities().getCities()) {
                Tiles.Tile cityCenter = c.getCityCenterTile();
                int numUnits = cityCenter.getUnits().size();
                if (numUnits == 0) {
                    cities.add(c);
                } else if (numUnits < 3) {
                    Tiles.Tile[] nearbyTiles = cityCenter.getTilesExactlyInRange(2);
                    for (Tiles.Tile t : nearbyTiles) {
                        if (t != null) {
                            Nation nationality = t.getOwnedNation();
                            if (nationality != null && nationality != this.nation) {
                                cities.add(c);
                            }
                        }
                    }
                }
            }
            threatenedCitiesListCache = cities;
            return cities;
        }
        return threatenedCitiesListCache;
    }

    private void patrolBorders(Unit unit) {
        // Implement logic for defensive units to patrol the nation's borders
        Tiles.Tile[] tiles = unit.getTile().getTilesInRange(4);
        for(Tiles.Tile tile : tiles) {
            if(tile!=null && tile.getNationality() == this.nation && tile.isBorderEdge() && tile != unit.getTile() && unit.setPath(tile)) {
                break;
            }
        }
    }

    private Tiles.Tile findBestAttackTarget(Unit unit) {
        // Implement a more sophisticated algorithm to find the best attack target
        // Consider factors like enemy strength, strategic value, etc.
        if(unit.getTile().getOwnedNation() == null || unit.getNation() == unit.getTile().getOwnedNation()) {
            for(int i = 3; i < 12; i+=2) {
                Tiles.Tile[] tiles = unit.getTile().getTilesExactlyInRange(i);
                for (Tiles.Tile tile : tiles) {
                    if (tile != null && tile.getOwnedNation() != null && tile.getOwnedNation() != this.nation && tile != unit.getTile() && unit.setPath(tile)) {
                        return tile;
                    }
                }
            }
        }  else {
            for(int i = 1; i < 3; i+=1) {
                Tiles.Tile[] tiles = unit.getTile().getTilesExactlyInRange(i);
                for (Tiles.Tile tile : tiles) {
                    if (tile != null && tile.getOwnedNation() != this.nation && (tile.hasCityCenter()) && unit.setPath(tile)) {
                        return tile;
                    }
                }
            }
        }
        return null;
    }

    private void exploreTerritory(Unit unit) {
        // Implement logic for units to explore unexplored territory
        unit.setPath(world.getTiles().getRandomTile());
    }

    private void manageDiplomacy() {
        // Implement basic diplomacy logic
        // For example, propose trade deals, form alliances, or declare war based on AI's goals
    }
}