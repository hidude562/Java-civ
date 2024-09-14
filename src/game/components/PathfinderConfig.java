package game.components;

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
        boolean tileTypeFound = false;

        // Check for if the tile the unit can move on the specific tile type
        for (int type : tileTypes)
            if (tile.getType() == type) {
                tileTypeFound = true;
                break;
            }
        if ((exclusiveMovement && tileTypeFound) || (!exclusiveMovement && !tileTypeFound)) {
            return false;
        }
        return true;
    }
}
