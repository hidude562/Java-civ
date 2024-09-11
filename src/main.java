import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


abstract class TileType {
    abstract public int getType();
    abstract public AssortedUnitUnits getUnits();
    abstract public String toString();
    abstract public void setType(int type);
    abstract public void addUnit(Nation.Unit unit);
    abstract public Nation.Unit getUnit(int index);
    abstract public void removeUnit(Nation.Unit unit);
    abstract public void setCityCenter(Nation.City city);
    abstract public Nation.City getCityCenter();
    abstract public boolean hasCityCenter();
    abstract public void setOwnedCity(Nation.City ownedCity);
    abstract public Nation.City getOwnedCity();
    abstract public boolean hasOwnedCity();
    abstract public void setWorked(boolean worked);
    abstract public boolean getWorked();
    abstract public Yields getYields();
    // TODO: sadboy abstract
    // abstract public void popularMonster(sadBoy _sadBoy);
}

abstract class GameElement {
    abstract public void nextTurn();
}

class Vert2D {
    private int x;
    private int y;
    public Vert2D() {
        this.x = 0;
        this.y = 0;
    }
    public Vert2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX(){return x;};
    public int getY(){return y;};
    public static Vert2D delta(Vert2D a, Vert2D b) {
        return new Vert2D(b.getX() - a.getX(), b.getY() - a.getY());
    }
    public static Vert2D add(Vert2D a, Vert2D b) {
        return new Vert2D(a.getX() + b.getX(), a.getY() + b.getY());
    }
    public static Vert2D abs(Vert2D a) {return new Vert2D(Math.abs(a.getX()), Math.abs(a.getY()));}
    public String toString() {return String.format("(%d, %d)", x, y);}
    public boolean equals(Vert2D o) {return o.getX() == x && o.getY() == y;}
}

class PathfinderConfig {
    // If true, the movement excludes the values from tileTypes
    // False, and it starts from nothing and includes
    private boolean exclusiveMovement = true;
    private int[] tileTypes;
    public PathfinderConfig() {
        this.tileTypes = new int[0];
    }
    public PathfinderConfig(boolean exclusiveMovement, int[] tileTypes) {
        this.tileTypes = tileTypes;
        this.exclusiveMovement = exclusiveMovement;
    }
    public boolean canGoOnTile(Tiles.Tile tile) {
        boolean tileTypeFound=false;

        // Check for if the tile the unit can move on the specific tile type
        for(int type : tileTypes) if (tile.getType() == type) {tileTypeFound=true; break;}
        if((exclusiveMovement && tileTypeFound) || (!exclusiveMovement && !tileTypeFound)) {
            return false;
        }
        return true;
    }
}

class SpecialMoveConfig {
    private final boolean killOnUse;
    private String name;
    private int implementationId;

    public SpecialMoveConfig(String name, boolean killOnUse, int implementationId) {
        this.name = name;
        this.killOnUse = killOnUse;
    }
    public String getName() {return name;}
    public boolean getKillOnUse() {return killOnUse;}
    public int getImplementationId() {return implementationId;}
}

// TODO: Unit kinda acts like a facade for unitconfig, so an abstract class can be used for better management
class UnitConfig {
    // Type of Nation.Unit
    private final String name;
    private final int startingMovement;
    private final int attack;
    private final int defense;
    private final int[] idsSpecials;
    PathfinderConfig pathfinderConfig;

    // IDs for the special
    public static final SpecialMoveConfig[] specials = {
            new SpecialMoveConfig("Build city", true, 0)
    };

    public UnitConfig(String name, int startingMovement, int attack, int defense, int[] idsSpecials, PathfinderConfig pathfinderConfig) {
        this.name = name;
        this.startingMovement = startingMovement;
        this.attack = attack;
        this.defense = defense;
        this.idsSpecials = idsSpecials;
        this.pathfinderConfig = pathfinderConfig;
    }
    public String getName() {return name;}
    public int getStartingMovement() {return startingMovement;}
    public int getAttack() {return attack;}
    public int getDefense() {return defense;}
    public SpecialMoveConfig[] getUnitSpecials() {
        SpecialMoveConfig[] unitSpecials = new SpecialMoveConfig[idsSpecials.length];
        for (int i = 0; i < idsSpecials.length; i++) {
            unitSpecials[i] = specials[idsSpecials[i]];
        }
        return unitSpecials;
    }
    public SpecialMoveConfig getSpecial(int id) {return specials[idsSpecials[id]];}
    public int getLengthSpecials() {return idsSpecials.length;}
    public PathfinderConfig getPathfinderConfig() {return pathfinderConfig;}
}

class BuildingConfig {
    private int[] implementationIds;
    private String name;
    public BuildingConfig(int[] implementationIds, String name) {
        this.implementationIds = implementationIds;
        this.name = name;
    }
    public int[] getImplementationIds() {return implementationIds;}
    public String getName() {return name;}
}

class AssortedUnitUnits {
    ArrayList<Nation.Unit> units;
    public AssortedUnitUnits() {
        units = new ArrayList<Nation.Unit>();
    }
    public void addUnit(Nation.Unit u){units.add(u);}
    public Nation.Unit get(int i){return units.get(i);}
    public void remove(Nation.Unit u){units.remove(u);}
    public int size(){return units.size();}
}

// A nation
class Nation extends GameElement {
    // 0 -> Alexander the Great, 1 -> G. Washington, etc. Each have bonuses and allat.
    private int nation;
    private Cities cities;
    private Units units;
    public Nation(int nation) {this.nation = nation;this.cities = new Cities();this.units = new Units();}
    public Cities getCities() {return cities;}
    public void addUnit(Unit u){units.addUnit(u);}
    public Units getUnits(){return units;}
    public void nextTurn() {
        cities.nextTurn();
        units.nextTurn();
    }

    class Units extends GameElement{
        private ArrayList<Unit> units;
        public Units() {this.units = new ArrayList<Unit>();}
        public void addUnit(Unit unit) {units.add(unit);}
        public Unit getUnit(int index) {return units.get(index);}
        public ArrayList<Unit> getUnits() {return units;}
        public void nextTurn() {
            for(Unit u : units) {
                u.nextTurn();
            }
        }
    }

    /* Nation.Unit list reference at units.txt */
    class Unit extends GameElement {
        private int type;
        private boolean isDead;
        private int movementLeft;
        public static final UnitConfig[] idConfigs = {
                new UnitConfig("Settler", 2, 0, 0, new int[]{0}, new PathfinderConfig(true, new int[]{0,1})),
                new UnitConfig("Warrior", 1, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0,1})),
        };

        private Tiles.Tile tile;
        private ArrayList<Tiles.Tile> path = new ArrayList<>();

        public Unit(int type, Tiles.Tile tile) {
            this.type = type;
            this.tile = tile;
            this.movementLeft = getConfig().getStartingMovement();
            this.tile.addUnit(this);
        }
        public Unit(int type) {
            this.type = type;
            this.tile = null;
            this.movementLeft = getConfig().getStartingMovement();
        }
        public int getType() {return type;}
        public PathfinderConfig getPathfinderConfig() {return idConfigs[type].getPathfinderConfig();}
        public void setType(int type) {this.type = type;}
        public boolean getIsDead(){return isDead;}
        public void setIsDead(boolean isDead){this.isDead = isDead;}
        public UnitConfig getConfig() {return idConfigs[type];}
        public void unitAction(int idSpecial) {
            SpecialMoveConfig special = getConfig().getSpecial(idSpecial);
            int implementationId = special.getImplementationId();

            if(implementationId == 0) {
                // Build a city
                getCities().addCity(new City(tile, "Skibidiopolis"));
            }

            if(special.getKillOnUse()) {
                this.setIsDead(true);
            }
        }
        public boolean setPath(Tiles.Tile tile) {
            this.path = this.tile.pathFind(tile, getPathfinderConfig());
            if(this.path == null) {this.path = new ArrayList<Tiles.Tile>(); return false;}
            moveUntilCannot();
            return true;
        }
        private void moveUntilCannot() {
            while(this.movementLeft > 0 && this.path.size() > 0) {
                this.moveToTile(this.path.remove(0));
            }
        }
        public boolean pathIsEmpty() {return this.path.size() == 0;}
        // Moves from one point, to the next if it is valid
        public boolean moveToTile(Tiles.Tile tile) {
            int distanceToTile = distanceToTile(tile);
            if (distanceToTile != -1 && distanceToTile <= this.movementLeft) {
                this.tile.removeUnit(this);
                this.tile = tile;
                this.tile.addUnit(this);
                this.movementLeft-=distanceToTile;
                return true;
            }
            return false;
        }
        // How many squares (you can travel diagonally) until destination
        public int distanceToTile(Tiles.Tile tile) {
            // Pathfind to tile
            ArrayList<Tiles.Tile> path = this.tile.pathFind(tile, getPathfinderConfig());

            // If a path was not found, return false
            if(path==null) return -1;

            // If all are passed, return length
            return path.size();
        }
        public void nextTurn() {
            moveUntilCannot(); this.movementLeft = getConfig().getStartingMovement();
        }
    }

    class City extends GameElement {
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
                if(productionIsComplete()) completeProduction();
            }
            public boolean productionIsComplete() {return productionComplete >= productionNeeded; /* TODO: Just a test */}
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
                    cityCenterTile.addUnit((Unit) buildable);
                } else if (buildable instanceof City.Building) {
                    // Assuming there is an addBuilding method in the City class
                    addBuilding((City.Building) buildable);
                }

                productionComplete-=productionNeeded;
                setBuildable(null);
            }
        }

        public City(Tiles.Tile cityCenterTile, String name) {
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
            if(workedTiles.size() >= population) return false;

            Vert2D delta = Vert2D.delta(cityCenterTile.getPosition(), tile.getPosition());
            int dist = Math.max(Math.abs(delta.getX()), Math.abs(delta.getY()));
            if(dist == 0 || dist > range) return false;

            if(tile.getWorked()) return false;
            if(tile.getOwnedCity() != this && tile.getOwnedCity() != null) return false;

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

                    if(t1==null) t1Yields=-1; else t1Yields = t1.getYields().getTotal();
                    if(t2==null) t2Yields=-1; else t2Yields = t2.getYields().getTotal();

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
            if(!workedTiles.contains(tile)) return false;
            if(!tileInCitySphereOfInfluence(tile)) tile.setOwnedCity(null);
            tile.setWorked(false);
            workedTiles.remove(tile);
            return true;
        }
        private Yields getYields() {
            // Get Tile yields
            Yields tileYields = new Yields(name, 0, 0, 0, 0);
            for(Tiles.Tile tile : workedTiles) {
                tileYields = tileYields.add(tile.getYields());
            }

            // Get Citizen yields
            Yields citizenYields = new Yields(name, 0, population-workedTiles.size(), population, population);
            Yields yields = tileYields.add(citizenYields);
            return yields;
        }
        public void collectYields() {
            Yields yields = getYields();
            builder.increaseProductionComplete(yields.getProduction());
            addFoodPopulus(yields.getFood());
        }
        public void addFoodPopulus(int food) {
            collectedFood+=food;
            int foodToIncrement = getFoodToIncrementPopulation();
            if(collectedFood >= foodToIncrement) {
                collectedFood-=foodToIncrement;
                population++;
            }
        }
        public int getCollectedFood() {return collectedFood;}
        public int getFoodToIncrementPopulation() {return population*5;}
        public void addBuilding(City.Building building) {
            this.buildings.add(building);
        }
        public void setProduction(Object buildable) {builder.setBuildable(buildable);}
        public boolean isBuildingSomething() {return builder.getBuildable() != null;}
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("--------------------\n");
            sb.append("The grandious city of "+name);
            sb.append("\nPopulation: "+population);
            sb.append("\nRange: "+range);
            sb.append("\nBuildings:");
            for(City.Building building : buildings) {
                sb.append("\n  "+building.toString());
            }
            sb.append("\nWorked tiles:");
            for(Tiles.Tile tile : workedTiles) {
                sb.append("\n  "+tile.toString());
            }
            sb.append("\nYields: "+getYields());
            sb.append("\n--------------------\n");
            return sb.toString();
        }
        public void nextTurn() {
            collectYields();
            autoAssignWorkers();
        }
    }

    static class Cities extends GameElement {
        private ArrayList<City> cities;
        public Cities(){this.cities = new ArrayList<City>();}
        public Cities(ArrayList<City> cities){this.cities = cities;}
        public void addCity(City city) {cities.add(city);}
        public City getCity(int index){return cities.get(index);};
        public ArrayList<City> getCities(){return cities;};
        public void nextTurn() {
            for(City city : cities) {
                city.nextTurn();
            }
        }
    }


}

// Wrapper for a List of Nations
class Nations extends GameElement {
    ArrayList<Nation> nations = new ArrayList<>();
    public Nations(Tiles tiles, int numCivs){
        for(int i = 0; i < numCivs; i++) {
            Nation nation = new Nation(i);
            Tiles.Tile tile = tiles.getRandomTile();
            while(tile.getType()<=1){
                tile = tiles.getRandomTile();
            }
            tile = tiles.getTile(tiles.getIndexFromPoint(new Vert2D(1,2)));

            Nation.Unit initalSettler = nation.new Unit(0, tile);

            nation.addUnit(initalSettler);
            nations.add(nation);
        }
    };
    public Nations(ArrayList<Nation> nations){this.nations = nations;};
    public Nation getNation(int index){return nations.get(index);};
    public ArrayList<Nation> getNations(){return nations;};
    public void nextTurn() {
        for(Nation nation : nations) {
            nation.nextTurn();
        }
    }
}

class Yields {
    int food;
    int production;
    int science;
    int gold;
    String name;

    public Yields(String name, int food, int production, int science, int gold) {
        this.food = food;
        this.production = production;
        this.science = science;
        this.gold = gold;
    }

    public String getName() {return name;}
    public int getFood() {return food;}
    public int getProduction() {return production;}
    public int getScience() {return science;}
    public int getGold() {return gold;}
    public int getTotal() {return food+production+science+gold;}
    public static Yields addYields(Yields y1, Yields y2) {
        return new Yields(y1.getName(), y1.getFood()+y2.getFood(), y1.getProduction()+y2.getProduction(), y1.getScience()+y2.getScience(), y1.getGold()+y2.getGold());
    }
    public Yields add(Yields y) {
        return Yields.addYields(this, y);
    }
    public String toString() {
        return String.format("%s: Food: %d, Production: %d, Science: %d, Gold: %d", name, food, production, science, gold);
    }
}

// The data stored for the tiles
// Don't use this for getting tile data, use Tiles.tile
class TileData extends TileType {
    private int type;
    public static final Yields[] tileYields = new Yields[]{
            new Yields("Ocean", 1, 0, 0, 0),
            new Yields("Coast", 1, 0, 1, 0),
            new Yields("Ice",0, 0, 0, 0),
            new Yields("Tundra", 1, 0, 0, 0),
            new Yields("Grassland", 2, 0, 0, 0),
            new Yields("Plains", 1, 1, 0, 0),
            new Yields("Desert", 0, 0, 0, 1)
    };
    private AssortedUnitUnits units;
    private Nation.City cityCenter = null;
    private Nation.City ownedCity  = null;
    private boolean worked  = false;
    public TileData() {units = new AssortedUnitUnits();}
    public TileData(int type) {this.type = type;}
    private TileData tileData(){return this;}
    public int getType() {return type;}
    public AssortedUnitUnits getUnits() {return units;}
    public String toString() {return String.valueOf(getType());}
    public void setType(int type) {this.type=type;}
    public void addUnit(Nation.Unit unit) {units.addUnit(unit);}
    public Nation.Unit getUnit(int index) {return units.get(index);}
    public void removeUnit(Nation.Unit unit) {
        units.units.remove(unit);
    }
    public void setCityCenter(Nation.City city) {this.cityCenter = city;}
    public Nation.City getCityCenter() {return cityCenter;}
    public boolean hasCityCenter() {return cityCenter != null;}
    public void setOwnedCity(Nation.City ownedCity) {this.ownedCity = ownedCity;}
    public Nation.City getOwnedCity() {return this.ownedCity;}
    public boolean hasOwnedCity() {return this.ownedCity != null;}
    public void setWorked(boolean worked) {this.worked = worked;}
    public boolean getWorked() {return worked;}
    public Yields getYields() {return tileYields[getType()];}
}

// Stores the actual tiles data, and use for passing references
class Tiles {
    TileData[] tiles;
    Vert2D mapSize;

    public Tiles(Vert2D mapSize) {
        // Create a map of tiles
        tiles = new TileData[mapSize.getX()*mapSize.getY()];
        for(int i = 0; i < mapSize.getY()*mapSize.getX(); i++) {
            tiles[i] = new TileData();
        }
        this.mapSize = mapSize;
    }

    // Returns not tile data, but the reference to the tile and acts like it is real, so it has additional access to cool features
    public Tile getTile(int referenceIndex){
        if(referenceIndex < 0 || referenceIndex >= mapSize.getX()*mapSize.getY()) {
            return null;
        }
        return new Tile(referenceIndex);
    }
    public void setTileType(int type, int referenceIndex){tiles[referenceIndex].setType(type);}
    public Vert2D getPointfromIndex(int index){return new Vert2D(index%mapSize.getX(), index/mapSize.getX());}
    public int getIndexFromPoint(Vert2D point){return point.getX() % mapSize.getX() + point.getY() * mapSize.getX();}
    public Tile getRandomTile() {return getTile((int) (Math.random() * mapSize.getX()*mapSize.getY()));}
    public void newWorld() {
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                setTileType(
                        (x/4+y/4+1)%2*4,
                        getIndexFromPoint(
                            new Vert2D(
                                    x, y
                            )
                        )
                );
            }
        }
    }

    // Reference that acts like tile data
    // Accesses the reference from the upper Tiles class.
    class Tile extends TileType {
        private final int referenceIndex;
        public Tile(int referenceIndex){
            this.referenceIndex = referenceIndex;
        };
        private TileData tileData(){return tiles[referenceIndex];}
        public int getType() {return tileData().getType();}
        public void setType(int type) {tileData().setType(type);}
        public AssortedUnitUnits getUnits() {return tileData().getUnits();}
        public void addUnit(Nation.Unit u) {tileData().addUnit(u);}
        public Nation.Unit getUnit(int index) {return tileData().getUnit(index);}
        public void setCityCenter(Nation.City city) {tileData().setCityCenter(city);}
        public Nation.City getCityCenter() {return tileData().getCityCenter();}
        public boolean hasCityCenter() {return tileData().hasCityCenter();}
        public void removeUnit(Nation.Unit unit) {
            tileData().removeUnit(unit);
        }
        public void setOwnedCity(Nation.City ownedCity) {tileData().setOwnedCity(ownedCity);}
        public Nation.City getOwnedCity() {return tileData().getOwnedCity();}
        public boolean hasOwnedCity() {return tileData().hasOwnedCity();}
        public void setWorked(boolean worked) {tileData().setWorked(worked);}
        public boolean getWorked() {return tileData().getWorked();}
        public Yields getYields() {return tileData().getYields();}
        public Vert2D getPosition() {return getPointfromIndex(referenceIndex);}
        public int getReferenceIndex() {return referenceIndex;}
        public Tile getTileFromRelativeXY(Vert2D offset) {
            try {
                /*
                    TODO: for wrapping the world around, the index should
                    be decremented by world width for going too right as example
                */
                return getTile(referenceIndex +
                        getIndexFromPoint(offset));
            } catch(Exception e) {
                return null;
            }
        }
        public ArrayList<Tiles.Tile> pathFind(Tiles.Tile targetTile, PathfinderConfig pathFinderConfig) {
            // TODO: Better pathfinding
            ArrayList<Tiles.Tile> path = new ArrayList<>();
            Tiles.Tile currentTile = this;

            while(!targetTile.getPosition().equals(currentTile.getPosition())) {
                Vert2D delta = Vert2D.delta(targetTile.getPosition(), currentTile.getPosition());
                Vert2D optimalDirection = new Vert2D(
                        (delta.getX() == 0 ? 0 : 1) * (delta.getX() > 0 ? -1 : 1),
                        (delta.getY() == 0 ? 0 : 1) * (delta.getY() > 0 ? -1 : 1)
                );
                Vert2D direction = new Vert2D();

                if(pathFinderConfig.canGoOnTile(currentTile.getTileFromRelativeXY(optimalDirection))) {direction = optimalDirection;}
                else {
                    Tiles.Tile[] neighbors = currentTile.getNeighborTiles();

                    // 4 Trillion IQ
                    // It checks the corners first by incrementing by 2 (and ignoring middle tile)
                    // Then it wraps around for the main parts
                    for (int i = 0; i < 9 * 2; i += 2) {
                        // Check corners first
                        if (i != 4) {
                            Tiles.Tile neighbor = neighbors[i % 9];
                            if (neighbor != null && pathFinderConfig.canGoOnTile(neighbor)) {
                                Vert2D neighborDirection = Vert2D.delta(
                                        currentTile.getPosition(),
                                        neighbor.getPosition()
                                );
                                Vert2D deltaTargetDir = Vert2D.delta(optimalDirection, neighborDirection);
                                deltaTargetDir = Vert2D.abs(deltaTargetDir);
                                //System.out.println(deltaTargetDir);
                                if ((deltaTargetDir.getX() < 1 && deltaTargetDir.getY() < 2) || (deltaTargetDir.getX() < 2 && deltaTargetDir.getY() < 1)) {
                                    System.out.println(neighborDirection);
                                    direction = neighborDirection;
                                    break;
                                }
                            }
                        }
                    }
                }
                currentTile = currentTile.getTileFromRelativeXY(direction);
                if(!pathFinderConfig.canGoOnTile(currentTile) || direction.equals(new Vert2D()))
                    return null;

                path.add(currentTile);
            }
            return path;
        }
        public Tile[] getNeighborTiles(){
            // Get corners, and main directions
            Tile[] tiles = new Tile[9];

            for(int i = 0; i < 9; i++) {
                Vert2D direction = new Vert2D(i%3-1,i/3-1);
                if(!direction.equals(new Vert2D())) {
                    Tile tile = getTileFromRelativeXY(direction);
                    tiles[i] = tile;
                }
            }

            return tiles;
        }
        public String toString() {
            if(tileData().hasCityCenter()) {
                return "C ";
            } else if(tileData().getWorked()){
                return "W ";
            } else if(tileData().hasOwnedCity()){
                return ": ";
            } else if (tileData().getUnits().size() > 0) {
                return "U" + tileData().getUnits().size(); // Assuming the first unit's type ID represents the unit for display
            } else if (getType() == 0) {
                return ". "; // Represents one type of tile
            } else if (getType() == 1) {
                return "# "; // Represents another type of tile
            }
            return "? "; // Default character for unknown types
        }
    }

    public String toString() {
        StringBuilder val = new StringBuilder();
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                val.append(getTile(getIndexFromPoint(
                        new Vert2D(
                                x, y
                        )
                )).toString());
            }
            val.append("\n");
        }
        return val.toString();
    }
}
class World extends GameElement {
    // Nations of the world
    private Nations nations;

    // Tiles
    private Tiles tiles;

    private int turn = 0;

    public World(Vert2D size) {
        tiles = new Tiles(size);
        tiles.newWorld();
        nations =  new Nations(tiles, 1);
    }
    public Nations getNations() {return nations;}
    public Tiles getTiles() {return tiles;}
    public int getTurn() {return turn;}
    public void nextTurn() {turn++; nations.nextTurn();}
    public String toString() {
        return tiles.toString();
    }
}
public class main {
    public static void main(String[] args) {
        World world = new World(new Vert2D(
                8, 8
        ));
        System.out.println(world);
        Nation playerNation = world.getNations().getNation(0);
        Nation.Unit settler = playerNation.getUnits().getUnit(0);

        while(true) {
            settler.setPath(
                    world.getTiles().getTile(world.getTiles().getIndexFromPoint(
                            new Vert2D(2, 7)
                    ))
            );
            System.out.println(world);
            world.nextTurn();
            if (settler.pathIsEmpty()) break;
        }
        System.out.println(world);
        settler.unitAction(0);
        System.out.println(world);
        Nation.City userCity = playerNation.getCities().getCity(0);
        System.out.println(userCity.toString());
        userCity.setProduction(playerNation.new Unit(0));
        for(int i = 0; i < 10; i++) {
            world.nextTurn();
            System.out.println(userCity);
            userCity.autoAssignWorkers();
            System.out.println(world);
        }
    }
}