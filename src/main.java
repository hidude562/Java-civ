import java.util.ArrayList;


abstract class TileType {
    abstract public int getType();
    abstract public AssortedUnitUnits getUnits();
    abstract public String toString();
    abstract public void setType(int type);
    abstract public void addUnit(Unit unit);
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
}

class PathfinderConfig {
    // If true, the movement excludes the values from tileTypes
    // False, and it starts from nothing and includes
    private boolean exclusiveMovement = true;
    private int[] tileTypes;
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

/* Unit list reference at units.txt */
class Unit {
    private int type;
    private boolean isDead;
    private int movementLeft;

    PathfinderConfig pathfinderConfig;

    private Tiles.Tile tile;
    public Unit(int type, Tiles.Tile tile) {this.type = type; this.tile = tile;}
    public int getType() {return type;}
    public boolean getIsDead(){return isDead;}

    // Moves from one point, to the next if it is valid
    public boolean moveToTile(Tiles.Tile tile) {
        int distanceToTile = distanceToTile(tile);
        if (distanceToTile != -1 && distanceToTile <= this.movementLeft) {
            this.tile = tile;
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

        // Can the path be traversed with the amount of movement left
        if(path.size() > movementLeft) return -1;

        // If all are passed, return length
        return path.size();
    }
}

class AssortedUnitUnits {
    ArrayList<Unit> units;
    public AssortedUnitUnits() {
        units = new ArrayList<Unit>();
    }
    public void addUnit(Unit u){units.add(u);}
    public Unit getUnit(int i){return units.get(i);}
    public int size(){return units.size();}
}

class City {
    // The name of the city
    private String name;
    // The Reference
    private ArrayList<Tiles.Tile> cityTiles;
    private int population;
    private Tiles.Tile cityCenterTile;
    public City(Tiles.Tile cityCenterTile, String name) {
        this.cityCenterTile = cityCenterTile;
        this.name = name;
    }
}
class Cities {
    private ArrayList<City> cities;
    public Cities(){}
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
    public Nation(int nation) {this.nation = nation;}
    public Cities getCities() {return cities;}
    public void addUnit(Unit u){units.addUnit(u);}
    public Units getUnits(){return units;}

    class Units {
        private ArrayList<Unit> units;
        public Units() {}
        public void addUnit(Unit unit) {units.add(unit);}
        public Unit getUnit(int index) {return units.get(index);}
        public ArrayList<Unit> getUnits() {return units;}
    }
}

// Wrapper for a List of Nations
class Nations {
    ArrayList<Nation> nations;
    public Nations(Tiles tiles, int numCivs){
        for(int i = 0; i < numCivs; i++) {
            Nation nation = new Nation(i);
            Tiles.Tile tile = tiles.getRandomTile();
            while(tile.getType()!=1){
                tile = tiles.getRandomTile();
            }

            Unit initalSettler = new Unit(0, tile);

            nation.addUnit(initalSettler);
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
    public TileData() {}
    public TileData(int type) {this.type = type;}
    public int getType() {return type;}
    public AssortedUnitUnits getUnits() {return units;}
    public String toString() {return String.valueOf(getType());}
    public void setType(int type) {this.type=type;}
}

// Stores the actual tiles data, and use for passing references
//
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
        public Vert2D getPosition() {return getPointfromIndex(referenceIndex);}
        public AssortedUnitUnits getUnits() {return tileData().getUnits();}
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
            ArrayList<Tiles.Tile> path = new ArrayList<Tiles.Tile>();
            Tiles.Tile currentTile = this;

            while(targetTile.getPosition() != currentTile.getPosition()) {
                Vert2D delta = Vert2D.delta(targetTile.getPosition(), currentTile.getPosition());
                Vert2D direction = new Vert2D();
                if(delta.getX() > 0)
                    direction = Vert2D.add(direction, new Vert2D(1,0));
                if(delta.getX() < 0)
                    direction = Vert2D.add(direction, new Vert2D(-1,0));
                if(delta.getY() > 0)
                    direction = Vert2D.add(direction, new Vert2D(0,1));
                if(delta.getY() < 0)
                    direction = Vert2D.add(direction, new Vert2D(1,-1));

                currentTile = currentTile.getTileFromRelativeXY(direction);
                if(!pathFinderConfig.canGoOnTile(currentTile))
                    return null;

                path.add(currentTile);
            }
            return path;
        }
        public String toString() {return String.valueOf(getType());}
    }

    public String toString() {
        String val = "";
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                val += getTile(getIndexFromPoint(
                        new Vert2D(
                                x, y
                        )
                )).toString();
            }
            val+="\n";
        }
        return val;
    }
}
class World {
    // Nations of the world
    Nations nations;

    // Tiles
    Tiles tiles;

    public World(Vert2D size) {
        tiles = new Tiles(size);
        tiles.newWorld();
        nations =  new Nations(tiles, 1);
    }
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
    }
}