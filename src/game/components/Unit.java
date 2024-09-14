package game.components;

import java.util.ArrayList;

/* Nation.Unit list reference at units.txt */
class Unit extends GameElement {
    private final Nation nation;
    private int type;
    private boolean isDead;
    private int movementLeft;
    public static final UnitConfig[] idConfigs = {
            new UnitConfig("Settler", 2, 0, 0, new int[]{0}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Warrior", 1, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
    };

    private Tiles.Tile tile;
    private ArrayList<Tiles.Tile> path = new ArrayList<>();

    public Unit(Nation nation, int type, Tiles.Tile tile) {
        this.nation = nation;
        this.type = type;
        this.tile = tile;
        this.movementLeft = getConfig().getStartingMovement();
        this.tile.addUnit(this);
    }

    public Unit(Nation nation, int type) {
        this.nation = nation;
        this.type = type;
        this.tile = null;
        this.movementLeft = getConfig().getStartingMovement();
    }

    public Unit(Nation nation, Unit u) {
        this.nation = nation;
        this.type = u.getType();
        this.tile = u.getTile();
        this.movementLeft = u.getConfig().getStartingMovement();
    }

    public Unit(Nation nation, Unit u, Tiles.Tile tile) {
        this.nation = nation;
        this.type = u.getType();
        this.tile = tile;
        this.movementLeft = u.getConfig().getStartingMovement();
    }

    public int getType() {
        return type;
    }

    public Tiles.Tile getTile() {
        return tile;
    }

    protected void setTile(Tiles.Tile tile) {
        this.tile = tile;
    }

    public Nation getNation() {
        return nation;
    }

    public PathfinderConfig getPathfinderConfig() {
        return idConfigs[type].getPathfinderConfig();
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public void setDead() {
        this.isDead = true;
        this.tile.removeUnit(this);
    }

    public UnitConfig getConfig() {
        return idConfigs[type];
    }

    public void unitAction(int idSpecial) {
        SpecialMoveConfig special = getConfig().getSpecial(idSpecial);
        int implementationId = special.getImplementationId();

        if (implementationId == 0) {
            // Build a city
            nation.getCities().addCity(new City(nation, tile, "Skibidiopolis"));
        }

        if (special.getKillOnUse()) {
            this.setDead();
        }
    }

    public boolean setPath(Tiles.Tile tile) {
        this.path = this.tile.pathFind(tile, getPathfinderConfig());
        if (this.path == null) {
            this.path = new ArrayList<Tiles.Tile>();
            return false;
        }
        moveUntilCannot();
        return true;
    }

    private void moveUntilCannot() {
        if (isDead) return;

        while (this.movementLeft > 0 && this.path.size() > 0) {
            this.moveToTile(this.path.remove(0));
        }
    }

    public boolean pathIsEmpty() {
        return this.path.size() == 0;
    }

    public boolean attack(Tiles.Tile tile) {
        if (isDead) return false;

        Unit otherUnit = tile.getUnit(0);

        if (otherUnit.getIsDead())
            return true;
        if (otherUnit.getNation() == this.getNation())
            return true;

        System.out.println("Atack!");
        if (!tile.hasUnit())
            return true;

        if (otherUnit.getConfig().getDefense() == 0 && this.getConfig().getAttack() > 0) {
            // Capture unit (Mark other unit for deletion and copy to new nation)
            nation.getUnits().addUnit(new Unit(nation, otherUnit));
            otherUnit.setDead();
            return true;
        }
        if (otherUnit.getConfig().getDefense() + ((int) Math.random() * 2) < this.getConfig().getAttack() + ((int) Math.random() * 2)) {
            setDead();
            return true;
        }
        setDead();
        return false;
    }

    // Moves from one point, to the next if it is valid
    public boolean moveToTile(Tiles.Tile tile) {
        if (isDead) return false;

        int distanceToTile = distanceToTile(tile);
        if (distanceToTile != -1 && distanceToTile <= this.movementLeft) {
            this.tile.removeUnit(this);
            if (tile.getNationality() != null && tile.getNationality() != nation) {
                attack(tile);
                this.movementLeft -= distanceToTile;
            } else {
                this.tile = tile;
                this.tile.addUnit(this);
                this.movementLeft -= distanceToTile;
            }
            return true;
        }
        return false;
    }

    // How many squares (you can travel diagonally) until destination
    public int distanceToTile(Tiles.Tile tile) {
        // Pathfind to tile
        ArrayList<Tiles.Tile> path = this.tile.pathFind(tile, getPathfinderConfig());

        // If a path was not found, return false
        if (path == null) return -1;

        // If all are passed, return length
        return path.size();
    }

    public void nextTurn() {
        moveUntilCannot();
        this.movementLeft = getConfig().getStartingMovement();
    }
}
