package game.components;

import java.util.ArrayList;

/* Nation.Unit list reference at units.txt */
class Unit extends GameElement {
    private final Nation nation;
    private GameThings.UnitConfigReference type;
    private boolean isDead;
    private int movementLeft;
    public static final UnitConfig[] idConfigs = GameThings.units;

    private Tiles.Tile tile;
    private ArrayList<Tiles.Tile> path = new ArrayList<>();

    public Unit(Nation nation, GameThings.UnitConfigReference type, Tiles.Tile tile) {
        this.nation = nation;
        this.type = type;
        this.tile = tile;
        this.movementLeft = getConfig().getStartingMovement();
        this.tile.addUnit(this);
    }

    public Unit(Nation nation, GameThings.UnitConfigReference type) {
        this.nation = nation;
        this.type = type;
        this.tile = null;
        this.movementLeft = getConfig().getStartingMovement();
    }

    public Unit(Nation nation, Unit u) {
        this.nation = nation;
        this.tile = u.getTile();
        this.type = u.getType();
        this.movementLeft = u.getConfig().getStartingMovement();
    }

    private GameThings.UnitConfigReference getUnitConfigReference() {
        return this.type;
    }

    public Unit(Nation nation, Unit u, Tiles.Tile tile) {
        this.nation = nation;
        this.type = u.getType();
        this.tile = tile;
        this.movementLeft = u.getConfig().getStartingMovement();
    }

    public GameThings.UnitConfigReference getType() {
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
        return getConfig().getPathfinderConfig();
    }

    public void setType(GameThings.UnitConfigReference type) {
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
        return (UnitConfig) type.get();
    }

    public void unitAction(int idSpecial) {
        SpecialMoveConfig special = getConfig().getSpecial(idSpecial);
        int implementationId = special.getImplementationId();

        if (implementationId == 0) {
            if(tile.canBuildCityHere()) {
                // Build a city
                if (nation.getCities().size() == 0) {
                    nation.addUnit(new Unit(nation, new GameThings.UnitConfigReference(1), tile));
                }
                nation.getCities().addCity(new City(nation, tile, "Skibidiopolis"));
            }
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

        while (this.movementLeft > 0 && !pathIsEmpty()) {
            this.moveToTile(this.path.remove(0));
        }
    }

    public boolean pathIsEmpty() {
        return this.path.size() == 0;
    }

    public int randomAttackAdder() {
        int add = 0;
        for(int i = 0; i < 5; i++) {
            add+=((int) Math.random() * 2);
        }
        return add;
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
        if (otherUnit.getConfig().getDefense() + randomAttackAdder() < this.getConfig().getAttack() + randomAttackAdder()) {
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

    public boolean canTravel(Tiles.Tile tile) {
        if(distanceToTile(tile) != -1) {return true;}
        return false;
    }
}
