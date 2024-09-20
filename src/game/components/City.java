package game.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class City extends GameElement {
    private Nation nation;
    // The name of the city
    private String name;
    // Tiles when culture higher, autoexpand
    private ArrayList<Tiles.Tile> cityTiles;
    // Worked tiles, can only be as much as populus
    // Range is just the ones directly next to/diagonal
    // Range can increase like Civ Rev courthouse with relatively early tech
    private ArrayList<Tiles.Tile> workedTiles;
    private int population;
    private Tiles.Tile cityCenterTile;
    private int range;
    private ArrayList<Building> buildings;
    // Used for building things in city (Units, buildings)
    private Builder builder;
    private int collectedFood = 0;

    public int getPopulation() {
        return population;
    }

    public Nation getNation() {
        return nation;
    }

    public Tiles.Tile getCityCenterTile() {
        return cityCenterTile;
    }

    public ArrayList<Tiles.Tile> getWorkedTiles() {
        return workedTiles;
    }


    class Building {
        public static BuildingConfig idConfigs[] = GameThings.buildings;
        private GameThings.BuildingConfigReference type;

        public Building(GameThings.BuildingConfigReference type) {
            this.type = type;

            for(int id : ((BuildingConfig) type.get()).getImplementationIds()) {
                switch(id) {
                    case 8:
                        City.this.range += 1;
                }
            }
        }

        public BuildingConfig getConfig() {
            return (BuildingConfig) type.get();
        }

        public String toString() {
            return getConfig().getName();
        }
    }

    class Builder {
        // For building Unit and buildings
        private int productionComplete;
        private GameThings.Reference buildable;
        // TODO: Remove when custom production for things are implementde
        private int productionNeeded = 20;

        public Builder(GameThings.Reference buildable) {
            this.buildable = buildable;
            this.productionComplete = 0;
        }

        public Builder() {
            this.buildable = null;
            this.productionComplete = 0;
        }

        public int getProductionComplete() {
            return this.productionComplete;
        }

        public void increaseProductionComplete(int production) {
            this.productionComplete += production;
            if (productionIsComplete()) completeProduction();
        }

        public boolean productionIsComplete() {
            if(buildable == null) return false;
            return productionComplete >= productionNeeded; /* TODO: Just a test */
        }

        public void setBuildable(GameThings.Reference buildable) {
            this.buildable = buildable;
            if(buildable != null)
                this.productionNeeded = ((Buildable) buildable.get()).getProduction();
            /*
            if(buildable.get() instanceof UnitConfig || buildable.get() instanceof BuildingConfig) {
                this.productionNeeded = ((Buildable) buildable.get()).getProduction();
            }

             */
        }

        public Object getBuildable() {
            return this.buildable;
        }

        private void completeProduction() {
            // Add the Building to the city, or the unit to the city's tile

            if (buildable instanceof GameThings.UnitConfigReference) {
                // Assuming there is a method to add Unit to a Tile in the City or Map class
                Unit u = new Unit(nation, (GameThings.UnitConfigReference) buildable, cityCenterTile);
                nation.getUnits().addUnit(u);
            } else if (buildable instanceof GameThings.BuildingConfigReference) {
                // Assuming there is an addBuilding method in the City class
                Building b = new Building((GameThings.BuildingConfigReference) buildable);
                addBuilding(b);
            }

            productionComplete -= productionNeeded;
            setBuildable(null);
        }
    }

    public City(Nation nation, Tiles.Tile cityCenterTile, String name) {
        this.nation = nation;
        this.cityCenterTile = cityCenterTile;
        cityCenterTile.setCityCenter(this);
        this.name = name;
        this.population = 2;
        this.cityTiles = new ArrayList<>();
        workedTiles = new ArrayList<>();
        this.range = 1;
        this.buildings = new ArrayList<>();
        this.builder = new Builder();

        autoAssignWorkers();
    }

    public boolean hasBuilding(GameThings.BuildingConfigReference type) {
        for(Building b : buildings) {
            if(b.getConfig() == type.get()) {
                return true;
            }
        }
        return false;
    }
    public boolean buildingMeetsPrereqs(GameThings.BuildingConfigReference type) {
        GameThings.BuildingConfigReference prereq = ((BuildingConfig) type.get()).getPrereq();
        if(prereq == null) return true;
        if(hasBuilding(
                prereq
        )) {
            return true;
        }
        return false;
    }
    public boolean canMakeBuilding(GameThings.BuildingConfigReference b) {
        return !hasBuilding(b) && buildingMeetsPrereqs(b);
    }
    public ArrayList<GameThings.BuildingConfigReference> getBuildingsCanMake() {
        ArrayList<GameThings.BuildingConfigReference> buildingsUnlocked =
                nation.getTechTree().getUnlocks().getBuildingRefs();
        ArrayList<GameThings.BuildingConfigReference> buildingsCanMake =
                new ArrayList<GameThings.BuildingConfigReference>();
        for(GameThings.BuildingConfigReference b : buildingsUnlocked) {
            if(canMakeBuilding(b)) {
                buildingsCanMake.add(b);
            }
        }
        return buildingsCanMake;
    }

    public boolean assignWorkTile(Tiles.Tile tile) {
        if (workedTiles.size() >= population) return false;

        Vert2D delta = Vert2D.delta(cityCenterTile.getPosition(), tile.getPosition());
        int dist = Math.max(Math.abs(delta.getX()), Math.abs(delta.getY()));
        if (dist == 0 || dist > range) return false;

        if (tile.getWorked()) return false;
        if (tile.getOwnedCity() != this && tile.getOwnedCity() != null) return false;
        if (tile.getNationality() != null && tile.getNationality() != nation) return false;
        if (tile.hasCityCenter()) return false;

        tile.setOwnedCity(this);
        tile.setWorked(true);
        workedTiles.add(tile);

        return true;
    }

    public void autoAssignWorkers() {
        Tiles.Tile[] neighbors = cityCenterTile.getTilesInRange(range);

        // Assume that yields are additive (sum of all yields)
        Arrays.sort(neighbors, new Comparator<Tiles.Tile>() {
            @Override
            public int compare(Tiles.Tile t1, Tiles.Tile t2) {
                int t1Yields;
                int t2Yields;

                if (t1 == null) t1Yields = -1;
                else t1Yields = t1.getYields().getTotal();
                if (t2 == null) t2Yields = -1;
                else t2Yields = t2.getYields().getTotal();

                return Integer.compare(t2Yields, t1Yields); // Descending order
            }
        });

        int assignedPopulation = 0;
        for (Tiles.Tile neighbor : neighbors) {
            if (neighbor != null && assignWorkTile(neighbor)) {
                assignedPopulation++;
                if (assignedPopulation >= population) break;
            }
        }
    }

    public boolean tileInCitySphereOfInfluence(Tiles.Tile tile) {
        // TODO:
        return false;
    }

    public boolean stopWorkTile(Tiles.Tile tile) {
        if (!workedTiles.contains(tile)) return false;
        if (!tileInCitySphereOfInfluence(tile)) tile.setOwnedCity(null);
        tile.setWorked(false);
        workedTiles.remove(tile);
        return true;
    }

    public Yields getYields() {
        // Get Tile yields
        Yields tileYields = new Yields(name, 0, 0, 0, 0, 0);
        for (Tiles.Tile tile : workedTiles) {
            tileYields = tileYields.add(tile.getYields());
        }

        // Get Citizen yields
        Yields citizenYields = new Yields(name, 0, population - workedTiles.size(), population, population, 0);
        Yields yields = tileYields.add(citizenYields);

        // Apply building modifiers
        for(Building b : buildings) {
            System.out.println(b.getConfig().getName());
            for(int id : b.getConfig().getImplementationIds()) {
                System.out.println(id);
                switch(id) {
                    case 0:
                        yields.setScience(yields.getScience() * 2);
                        break;
                    case 1:
                        yields.setScience(yields.getScience() * 2);
                        break;
                    case 2:
                        yields.setGold(yields.getGold() * 2);
                        break;
                    case 3:
                        yields.setGold(yields.getGold() * 2);
                        break;
                    case 4:
                        yields.setFood(yields.getFood() * 3 / 2);
                        break;
                    case 5:
                        yields.setCulture(yields.getCulture() + population);
                        break;
                    case 6:
                        yields.setCulture(yields.getCulture() + population);
                        break;
                }
            }
        }
        return yields;
    }

    public void collectYields() {
        Yields yields = getYields();
        builder.increaseProductionComplete(yields.getProduction());
        addFoodPopulus(yields.getFood());
    }

    public void addFoodPopulus(int food) {
        collectedFood += food;
        int foodToIncrement = getFoodToIncrementPopulation();
        if (collectedFood >= foodToIncrement) {
            collectedFood -= foodToIncrement;
            population++;
        }
    }

    public int getCollectedFood() {
        return collectedFood;
    }

    public int getFoodToIncrementPopulation() {
        return population * 5;
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    public void setProduction(GameThings.Reference buildable) {
        builder.setBuildable(buildable);
    }

    public boolean isBuildingSomething() {
        return builder.getBuildable() != null;
    }

    public Nation getNationality() {
        return nation;
    }

    public void exchangeOwnershipIfTaken() {
        Nation newOwnerNationality = this.cityCenterTile.getNationality();
        if (newOwnerNationality != null && newOwnerNationality != nation) {
            System.out.println("City taken!");
            // Remove from nation's cities
            nation.getCities().removeCity(this);
            // Change the references to the previous nation
            this.nation = newOwnerNationality;
            newOwnerNationality.getCities().addCity(this);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");
        sb.append("The grandious city of " + name);
        sb.append("\nPopulation: " + population);
        sb.append("\nRange: " + range);
        sb.append("\nBuildings:");
        for (Building building : buildings) {
            sb.append("\n  " + building.toString());
        }
        sb.append("\nWorked tiles:");
        for (Tiles.Tile tile : workedTiles) {
            sb.append("\n  " + tile.toString());
        }
        sb.append("\nYields: " + getYields());
        sb.append("\n--------------------\n");
        return sb.toString();
    }

    public void nextTurn() {
        System.out.println(this);
        collectYields();
        autoAssignWorkers();
        exchangeOwnershipIfTaken();
    }
}
