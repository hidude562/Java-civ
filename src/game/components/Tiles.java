package game.components;

import java.util.ArrayList;
import java.util.Random;

// Stores the actual tiles data, and use for passing references
class Tiles {
    TileData[] tiles;
    Vert2D mapSize;

    public Tiles(Vert2D mapSize) {
        // Create a map of tiles
        tiles = new TileData[mapSize.getX() * mapSize.getY()];
        for (int i = 0; i < mapSize.getY() * mapSize.getX(); i++) {
            tiles[i] = new TileData();
        }
        this.mapSize = mapSize;
    }

    // Returns not tile data, but the reference to the tile and acts like it is real, so it has additional access to cool features
    public Tile getTile(int referenceIndex) {
        if (referenceIndex < 0 || referenceIndex >= mapSize.getX() * mapSize.getY()) {
            return null;
        }
        return new Tile(referenceIndex);
    }

    public Tile getTile(Vert2D xy) {
        int referenceIndex = getIndexFromPoint(xy);
        if (referenceIndex < 0 || referenceIndex >= mapSize.getX() * mapSize.getY()) {
            return null;
        }
        return new Tile(referenceIndex);
    }

    public void setTileType(int type, int referenceIndex) {
        tiles[referenceIndex].setType(type);
    }

    public Vert2D getPointfromIndex(int index) {
        return new Vert2D(index % mapSize.getX(), index / mapSize.getX());
    }

    public int getIndexFromPoint(Vert2D point) {
        return point.getX() % mapSize.getX() + point.getY() * mapSize.getX();
    }

    public Tile getRandomTile() {
        return getTile((int) (Math.random() * mapSize.getX() * mapSize.getY()));
    }

    public void newWorld() {
        Random random = new Random();

        // First pass: Generate basic terrain
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                int tileType = random.nextInt(TileData.tileYields.length);
                setTileType(tileType, getIndexFromPoint(new Vert2D(x, y)));
            }
        }

        // Second pass: Smooth out the terrain
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                int currentType = getTile(getIndexFromPoint(new Vert2D(x, y))).getType();

                // Check neighboring tiles
                int[] neighborTypes = getNeighborTypes(x, y);
                int mostCommonType = getMostCommonType(neighborTypes);

                // 50% chance to change to the most common neighboring type
                if (random.nextDouble() < 0.5) {
                    setTileType(mostCommonType, getIndexFromPoint(new Vert2D(x, y)));
                }
            }
        }

        // Third pass: Add some randomness back
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                // 10% chance to change to a random type
                if (random.nextDouble() < 0.1) {
                    int newType = random.nextInt(TileData.tileYields.length);
                    setTileType(newType, getIndexFromPoint(new Vert2D(x, y)));
                }
            }
        }
    }

    private int[] getNeighborTypes(int x, int y) {
        int[] types = new int[8];
        int index = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < mapSize.getX() && ny >= 0 && ny < mapSize.getY()) {
                    types[index++] = getTile(getIndexFromPoint(new Vert2D(nx, ny))).getType();
                }
            }
        }
        return types;
    }

    private int getMostCommonType(int[] types) {
        int[] typeCounts = new int[TileData.tileYields.length];
        for (int type : types) {
            if (type >= 0) typeCounts[type]++;
        }
        int maxCount = 0;
        int mostCommonType = 0;
        for (int i = 0; i < typeCounts.length; i++) {
            if (typeCounts[i] > maxCount) {
                maxCount = typeCounts[i];
                mostCommonType = i;
            }
        }
        return mostCommonType;
    }

    // Reference that acts like tile data
    // Accesses the reference from the upper Tiles class.
    class Tile extends TileType {
        private final int referenceIndex;

        public Tile(int referenceIndex) {
            this.referenceIndex = referenceIndex;
        }

        private TileData tileData() {
            return tiles[referenceIndex];
        }

        public int getType() {
            return tileData().getType();
        }

        public void setType(int type) {
            tileData().setType(type);
        }

        public AssortedUnitUnits getUnits() {
            return tileData().getUnits();
        }

        public void addUnit(Unit u) {
            tileData().addUnit(u);
        }

        public boolean hasUnit() {
            return tileData().hasUnit();
        }

        public Unit getUnit(int index) {
            return tileData().getUnit(index);
        }

        public void setCityCenter(City city) {
            tileData().setCityCenter(city);
        }

        public City getCityCenter() {
            return tileData().getCityCenter();
        }

        public boolean hasCityCenter() {
            return tileData().hasCityCenter();
        }

        public void removeUnit(Unit unit) {
            tileData().removeUnit(unit);
        }

        public void setOwnedCity(City ownedCity) {
            tileData().setOwnedCity(ownedCity);
        }

        public City getOwnedCity() {
            return tileData().getOwnedCity();
        }

        public boolean hasOwnedCity() {
            return tileData().hasOwnedCity();
        }

        public void setWorked(boolean worked) {
            tileData().setWorked(worked);
        }

        public boolean getWorked() {
            return tileData().getWorked();
        }

        public Yields getYields() {
            return tileData().getYields();
        }

        public Vert2D getPosition() {
            return getPointfromIndex(referenceIndex);
        }

        public int getReferenceIndex() {
            return referenceIndex;
        }

        public Nation getNationality() {
            return tileData().getNationality();
        }

        public Tile getTileFromRelativeXY(Vert2D offset) {
            try {
                /*
                    TODO: for wrapping the world around, the index should
                    be decremented by world width for going too right as example
                */
                return getTile(referenceIndex +
                        getIndexFromPoint(offset));
            } catch (Exception e) {
                return null;
            }
        }

        public ArrayList<Tile> pathFind(Tile targetTile, PathfinderConfig pathFinderConfig) {
            // TODO: Better pathfinding (But still fast)

            /*
                This is a convex pathfinder algorithm
                It finds the best path in linear time
                But it will only always work when it is a convex shape
             */
            System.out.println("Skbidi");
            ArrayList<Tile> path = new ArrayList<>();
            Tile currentTile = this;
            if (!pathFinderConfig.canGoOnTile(targetTile)) return null;
            System.out.println("Maybe");

            while (!targetTile.getPosition().equals(currentTile.getPosition())) {
                Vert2D delta = Vert2D.delta(targetTile.getPosition(), currentTile.getPosition());
                Vert2D optimalDirection = new Vert2D(
                        (delta.getX() == 0 ? 0 : 1) * (delta.getX() > 0 ? -1 : 1),
                        (delta.getY() == 0 ? 0 : 1) * (delta.getY() > 0 ? -1 : 1)
                );
                Vert2D direction = new Vert2D();

                if (pathFinderConfig.canGoOnTile(currentTile.getTileFromRelativeXY(optimalDirection))) {
                    direction = optimalDirection;
                } else {
                    Tile[] neighbors = currentTile.getNeighborTiles();

                    // 4 Trillion IQ
                    // It checks the corners first by incrementing by 2 (and ignoring middle tile)
                    // Then it wraps around for the main parts
                    for (int i = 0; i < 9 * 2; i += 2) {
                        // Check corners first
                        if (i != 4) {
                            Tile neighbor = neighbors[i % 9];
                            if (neighbor != null && pathFinderConfig.canGoOnTile(neighbor)) {
                                Vert2D neighborDirection = Vert2D.delta(
                                        currentTile.getPosition(),
                                        neighbor.getPosition()
                                );
                                Vert2D deltaTargetDir = Vert2D.delta(optimalDirection, neighborDirection);
                                deltaTargetDir = Vert2D.abs(deltaTargetDir);
                                if ((deltaTargetDir.getX() < 1 && deltaTargetDir.getY() < 2) || (deltaTargetDir.getX() < 2 && deltaTargetDir.getY() < 1)) {
                                    direction = neighborDirection;
                                    break;
                                }
                            }
                        }
                    }
                }
                currentTile = currentTile.getTileFromRelativeXY(direction);
                if (!pathFinderConfig.canGoOnTile(currentTile) || direction.equals(new Vert2D()))
                    return null;

                path.add(currentTile);
                System.out.println(currentTile.getPosition());
            }
            return path;
        }

        public Tile[] getNeighborTiles() {
            // Get corners, and main directions
            Tile[] tiles = new Tile[9];

            for (int i = 0; i < 9; i++) {
                Vert2D direction = new Vert2D(i % 3 - 1, i / 3 - 1);
                if (!direction.equals(new Vert2D())) {
                    Tile tile = getTileFromRelativeXY(direction);
                    tiles[i] = tile;
                }
            }

            return tiles;
        }
        public Tile[] getTilesInRange(int range) {
            // Get corners, and main directions
            int distToCover = (1+range*2);
            Tile[] tiles = new Tile[distToCover*distToCover];
            for (int i = 0; i < distToCover*distToCover; i++) {
                Vert2D direction = new Vert2D(i % distToCover - range, i / distToCover - range);
                if (!direction.equals(new Vert2D())) {
                    Tile tile = getTileFromRelativeXY(direction);
                    tiles[i] = tile;
                }
            }

            return tiles;
        }
        public Tile[] getTilesExactlyInRange(int range) {
            // Do fancy math to get a spiral for it basically
            int distToCover = (range*2*4);
            Tile[] tiles = new Tile[distToCover];
            for (int i = 0; i < distToCover; i++) {
                int dx = (i%2*2-1);
                int dy =  ((i/2)%2*2-1);
                Vert2D direction = new Vert2D(
                        dx * range + (i/4 * -dx * ((i+1)%2)),
                        dy * range + (i/4 * -dy * (i%2))
                );
                Tile tile = getTileFromRelativeXY(direction);
                tiles[i] = tile;
            }

            return tiles;
        }
        public boolean isBorderEdge() {
            Tile[] tiles = getNeighborTiles();
            for(Tile t : tiles) {
                if(t != null && t.getNationality() != this.getNationality()) {
                    return true;
                }
            }
            return false;
        }
        public boolean canBuildCityHere() {
            if (tileData().getType() < 2)
                return false;
            if (tileData().hasCityCenter()) return false;

            Tile[] neighbors = getNeighborTiles();
            for (Tile t : neighbors) {
                if (t != null && t.hasCityCenter()) {
                    return false;
                }
            }

            return true;
        }

        public Tiles getMap() {return Tiles.this;}

        public String toString() {
            String repr = "";
            if (tileData().hasCityCenter()) {
                repr = "C";
            } else if (tileData().getUnits().size() > 0) {
                repr = "U" + tileData().getUnits().size();
            } else if (tileData().getWorked()) {
                repr = "W";
            } else if (tileData().hasOwnedCity()) {
                repr = ":";
            } else if (getType() == 0) {
                repr = ".";
            } else if (getType() == 1) {
                repr = "#";
            } else {
                repr = " ";
            }
            repr = String.format("%-3s",repr);
            return repr;
        }

        public Nation getOwnedNation() {
            // TODO: fix
            if(tileData() == null) return null;
            if(tileData().getOwnedCity() == null) return null;
            if(tileData().getOwnedCity().getNation() == null) return null;

            return tileData().getOwnedCity().getNation();
        }

        public City getClosestEnemyCity() {
            for(int i = 1; i < 10; i++) {
                Tile[] tiles = getTilesExactlyInRange(i);
                for(Tile t : tiles) {
                    if(t.hasCityCenter() && t.getOwnedNation() != this.getOwnedNation()) {
                        return t.getCityCenter();
                    }
                }
            }
            return null;
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
