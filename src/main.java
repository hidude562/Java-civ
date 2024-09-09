import java.util.ArrayList;


abstract class TileType {
    abstract public int getType();
    abstract public AssortedUnitUnits getUnits();
    abstract public String toString();
    abstract public void setType(int type);
    abstract public void addUnit(Nation.Unit unit);
    abstract public Nation.Unit getUnit(int index);
    abstract public void removeUnit(Nation.Unit unit);
    abstract public void setCityCenter(City city);
    abstract public City getCityCenter();
    abstract public boolean hasCityCenter();
    abstract public void setOwnedCity(City ownedCity);
    abstract public City getOwnedCity();
    abstract public boolean hasOwnedCity();
    // TODO: sadboy abstract
    // abstract public void popularMonster(sadBoy _sadBoy);
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

class UnitConfig {
    // Type of Nation.Unit
    private final String name;
    private final int startingMovement;
    private final int attack;
    private final int defense;
    private final int[] idsSpecials;
    // IDs for the special
    public static final SpecialMoveConfig[] specials = {
            new SpecialMoveConfig("Build city", true, 0)
    };

    public UnitConfig(String name, int startingMovement, int attack, int defense, int[] idsSpecials) {
        this.name = name;
        this.startingMovement = startingMovement;
        this.attack = attack;
        this.defense = defense;
        this.idsSpecials = idsSpecials;
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

class City {
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
    public City(Tiles.Tile cityCenterTile, String name) {
        this.cityCenterTile = cityCenterTile;
        cityCenterTile.setCityCenter(this);
        this.name = name;
        this.population = 2;
        this.cityTiles = new ArrayList<>();
    }
    public boolean workTile(Tiles.Tile tile) {
        if(workedTiles.size() >= population) return false;

        Vert2D delta = Vert2D.delta(cityCenterTile.getPosition(), tile.getPosition());
        int dist = Math.max(delta.getX(), delta.getY());
        if(dist == 0 || dist > range) return false;

         
        return true;
    }
}
class Cities {
    private ArrayList<City> cities;
    public Cities(){this.cities = new ArrayList<City>();}
    public Cities(ArrayList<City> cities){this.cities = cities;}
    public void addCity(City city) {cities.add(city);}
    public City getCity(int index){return cities.get(index);};
    public ArrayList<City> getCities(){return cities;};
}

// A nation
class Nation {
    // 0 -> Alexander the Great, 1 -> G. Washington, etc. Each have bonuses and allat.
    private int nation;
    private Cities cities;
    private Units units;
    public Nation(int nation) {this.nation = nation;this.cities = new Cities();this.units = new Units();}
    public Cities getCities() {return cities;}
    public void addUnit(Unit u){units.addUnit(u);}
    public Units getUnits(){return units;}

    class Units {
        private ArrayList<Unit> units;
        public Units() {this.units = new ArrayList<Unit>();}
        public void addUnit(Unit unit) {units.add(unit);}
        public Unit getUnit(int index) {return units.get(index);}
        public ArrayList<Unit> getUnits() {return units;}
    }

    /* Nation.Unit list reference at units.txt */
    class Unit {
        private int type;
        private boolean isDead;
        private int movementLeft;
        public static final UnitConfig[] idConfigs = {
                new UnitConfig("Settler", 20, 0, 0, new int[]{0}),
                new UnitConfig("Warrior", 1, 1, 1, new int[]{}),
        };

        PathfinderConfig pathfinderConfig;

        private Tiles.Tile tile;
        public Unit(int type, Tiles.Tile tile) {
            this.type = type;
            this.tile = tile;
            this.movementLeft = idConfigs[type].getStartingMovement();
            this.tile.addUnit(this);
            this.pathfinderConfig = new PathfinderConfig();
        }
        public int getType() {return type;}
        public void setType(int type) {this.type = type;}
        public boolean getIsDead(){return isDead;}
        public void setIsDead(boolean isDead){this.isDead = isDead;}
        public UnitConfig getConfig() {return idConfigs[type];}
        // I  hate doing this this way but we pass in
        // the nation for use for some things (Settlers for example)
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
            ArrayList<Tiles.Tile> path = this.tile.pathFind(tile, pathfinderConfig);

            // If a path was not found, return false
            if(path==null) return -1;

            // If all are passed, return length
            return path.size();
        }
    }
}

// Wrapper for a List of Nations
class Nations {
    ArrayList<Nation> nations = new ArrayList<>();
    public Nations(Tiles tiles, int numCivs){
        for(int i = 0; i < numCivs; i++) {
            Nation nation = new Nation(i);
            Tiles.Tile tile = tiles.getRandomTile();
            while(tile.getType()!=1){
                tile = tiles.getRandomTile();
            }

            Nation.Unit initalSettler = nation.new Unit(0, tile);

            nation.addUnit(initalSettler);
            nations.add(nation);
        }
    };
    public Nations(ArrayList<Nation> nations){this.nations = nations;};
    public Nation getNation(int index){return nations.get(index);};
    public ArrayList<Nation> getNations(){return nations;};
}

// The data stored for the tiles
// Don't use this for getting tile data, use Tiles.tile
class TileData extends TileType {
    private int type;
    private AssortedUnitUnits units;
    private City cityCenter = null;
    private City ownedCity  = null;
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
    public void setCityCenter(City city) {this.cityCenter = city;}
    public City getCityCenter() {return cityCenter;}
    public boolean hasCityCenter() {return cityCenter != null;}
    public void setOwnedCity(City ownedCity) {this.ownedCity = ownedCity;}
    public City getOwnedCity() {return this.ownedCity;}
    public boolean hasOwnedCity() {return this.ownedCity != null;}
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
    public Tile getTile(int referenceIndex){return new Tile(referenceIndex);}
    public void setTileType(int type, int referenceIndex){tiles[referenceIndex].setType(type);}
    public Vert2D getPointfromIndex(int index){return new Vert2D(index%mapSize.getX(), index/mapSize.getX());}
    public int getIndexFromPoint(Vert2D point){return point.getX() % mapSize.getX() + point.getY() * mapSize.getX();}
    public Tile getRandomTile() {return getTile((int) (Math.random() * mapSize.getX()*mapSize.getY()));}
    public void newWorld() {
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                setTileType(
                        (x/4+y/4+1)%2,
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
        public Tile(int referenceIndex){this.referenceIndex = referenceIndex;};
        private TileData tileData(){return tiles[referenceIndex];}
        public int getType() {return tileData().getType();}
        public void setType(int type) {tileData().setType(type);}
        public AssortedUnitUnits getUnits() {return tileData().getUnits();}
        public void addUnit(Nation.Unit u) {tileData().addUnit(u);}
        public Nation.Unit getUnit(int index) {return tileData().getUnit(index);}
        public void setCityCenter(City city) {tileData().setCityCenter(city);}
        public City getCityCenter() {return tileData().getCityCenter();}
        public boolean hasCityCenter() {return tileData().hasCityCenter();}
        public void removeUnit(Nation.Unit unit) {
            tileData().removeUnit(unit);
        }
        public void setOwnedCity(City ownedCity) {tileData().setOwnedCity(ownedCity);}
        public City getOwnedCity() {return tileData().getOwnedCity();}
        public boolean hasOwnedCity() {return tileData().hasOwnedCity();}
        public String toString() {
            if(tileData().hasCityCenter()) {
                return "C ";
            } else if (tileData().getUnits().size() > 0) {
                return "U" + tileData().getUnits().size(); // Assuming the first unit's type ID represents the unit for display
            } else if (getType() == 0) {
                return ". "; // Represents one type of tile
            } else if (getType() == 1) {
                return "# "; // Represents another type of tile
            }
            return "? "; // Default character for unknown types
        }
        public Vert2D getPosition() {return getPointfromIndex(referenceIndex);}
        public int getReferenceIndex() {return referenceIndex;}
        public Tile getTileFromRelativeXY(Vert2D offset) {
            try {
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
                Vert2D direction = new Vert2D();
                if(delta.getX() > 0)
                    direction = Vert2D.add(direction, new Vert2D(-1,0));
                if(delta.getX() < 0)
                    direction = Vert2D.add(direction, new Vert2D(1,0));
                if(delta.getY() > 0)
                    direction = Vert2D.add(direction, new Vert2D(0,-1));
                if(delta.getY() < 0)
                    direction = Vert2D.add(direction, new Vert2D(0,1));

                currentTile = currentTile.getTileFromRelativeXY(direction);
                if(!pathFinderConfig.canGoOnTile(currentTile))
                    return null;

                path.add(currentTile);
            }
            return path;
        }
        public Tile[] getNeighborTiles(){
            // Get corners, and main directions
            Tile[] tiles = new Tile[8];

            for(int i = 0; i < 9; i++) {
                Vert2D direction = new Vert2D(i%3-1,i/3-1);
                if(!direction.equals(new Vert2D()))
                    tiles[i] = getTileFromRelativeXY(direction);
            }

            return tiles;
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
class World {
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
    public void nextTurn() {turn++;}
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
        Nation.Unit settler = world.getNations().getNation(0).getUnits().getUnit(0);
        boolean moved = settler.moveToTile(
                world.getTiles().getTile(0)
        );
        System.out.println(world);
        settler.unitAction(0);
        System.out.println(world);
    }
}