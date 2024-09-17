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
        public static BuildingConfig idConfigs[] = {
                new BuildingConfig(new int[]{0}, "Library"),
                new BuildingConfig(new int[]{1}, "Market")
        };
        private int type;

        public Building(int type) {
            this.type = type;
        }

        public String toString() {
            return idConfigs[type].getName();
        }
    }

    // TODO: This structure could be used for science too
    class Builder {
        // For building Unit and buildings
        private int productionComplete;
        private Object buildable;
        // TODO: Remove when custom production for things are implementde
        private int productionNeeded = 20;

        public Builder(Object buildable) {
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
            return productionComplete >= productionNeeded; /* TODO: Just a test */
        }

        public void setBuildable(Object buildable) {
            this.buildable = buildable;
        }

        public Object getBuildable() {
            return this.buildable;
        }

        private void completeProduction() {
            // Add the Building to the city, or the unit to the city's tile

            if (buildable instanceof Unit) {
                // Assuming there is a method to add Unit to a Tile in the City or Map class
                Unit u = new Unit(nation, (Unit) buildable, cityCenterTile);
                nation.getUnits().addUnit(u);
            } else if (buildable instanceof Building) {
                // Assuming there is an addBuilding method in the City class
                addBuilding((Building) buildable);
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

    public boolean assignWorkTile(Tiles.Tile tile) {
        if (workedTiles.size() >= population) return false;

        Vert2D delta = Vert2D.delta(cityCenterTile.getPosition(), tile.getPosition());
        int dist = Math.max(Math.abs(delta.getX()), Math.abs(delta.getY()));
        if (dist == 0 || dist > range) return false;

        if (tile.getWorked()) return false;
        if (tile.getOwnedCity() != this && tile.getOwnedCity() != null) return false;
        if (tile.hasCityCenter()) return false;

        tile.setOwnedCity(this);
        tile.setWorked(true);
        workedTiles.add(tile);

        return true;
    }

    public void autoAssignWorkers() {
        // TODO: Courthouse support

        Tiles.Tile[] neighbors = cityCenterTile.getNeighborTiles();

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
        Yields tileYields = new Yields(name, 0, 0, 0, 0);
        for (Tiles.Tile tile : workedTiles) {
            tileYields = tileYields.add(tile.getYields());
        }

        // Get Citizen yields
        Yields citizenYields = new Yields(name, 0, population - workedTiles.size(), population, population);
        Yields yields = tileYields.add(citizenYields);
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

    public void setProduction(Object buildable) {
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
        collectYields();
        autoAssignWorkers();
        exchangeOwnershipIfTaken();
    }
}
