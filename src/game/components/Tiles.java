package game.components;

import java.util.ArrayList;

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
        // TODO: Just a test
        for (int y = 0; y < mapSize.getY(); y++) {
            for (int x = 0; x < mapSize.getX(); x++) {
                setTileType(
                        4,
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
            ArrayList<Tile> path = new ArrayList<>();
            Tile currentTile = this;
            if (!pathFinderConfig.canGoOnTile(targetTile)) return null;

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
        public boolean isBorderEdge() {
            Tile[] tiles = getNeighborTiles();
            for(Tile t : tiles) {
                if(t.getNationality() != this.getNationality()) {
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
