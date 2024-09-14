package game.components;

import java.util.ArrayList;

// A nation
class Nation extends GameElement {
    // 0 -> Alexander the Great, 1 -> G. Washington, etc. Each have bonuses and allat.
    private int nation;
    private Cities cities;
    private Units units;

    public Nation(int nation) {
        this.nation = nation;
        this.cities = new Cities();
        this.units = new Units();
    }

    public Cities getCities() {
        return cities;
    }

    public void removeCity(City c) {
        cities.removeCity(c);
    }

    public void addUnit(Unit u) {
        units.addUnit(u);
    }

    public Units getUnits() {
        return units;
    }

    public void nextTurn() {
        cities.nextTurn();
        units.nextTurn();
    }

    class Units extends GameElement {
        private ArrayList<Unit> units;

        public Units() {
            this.units = new ArrayList<Unit>();
        }

        public void addUnit(Unit unit) {
            units.add(unit);
        }

        public Unit getUnit(int index) {
            return units.get(index);
        }

        public ArrayList<Unit> getUnits() {
            return units;
        }

        public void nextTurn() {
            for (int i = 0; i < units.size(); i++) {
                Unit u = units.get(i);
                if (u.getIsDead()) {
                    units.remove(i);
                    i--;
                }
                u.nextTurn();
            }
        }

        public int size() {
            return units.size();
        }
    }

    /* Nation.Unit list reference at units.txt */
    class Unit extends GameElement {
        private int type;
        private boolean isDead;
        private int movementLeft;
        public static final UnitConfig[] idConfigs = {
                new UnitConfig("Settler", 2, 0, 0, new int[]{0}, new PathfinderConfig(true, new int[]{0, 1})),
                new UnitConfig("Warrior", 1, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
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

        public Unit(Unit u) {
            this.type = u.getType();
            this.tile = u.getTile();
            this.movementLeft = u.getConfig().getStartingMovement();
        }

        public Unit(Unit u, Tiles.Tile tile) {
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
            return Nation.this;
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

        public void setIsDead(boolean isDead) {
            this.isDead = isDead;
        }

        public UnitConfig getConfig() {
            return idConfigs[type];
        }

        public void unitAction(int idSpecial) {
            SpecialMoveConfig special = getConfig().getSpecial(idSpecial);
            int implementationId = special.getImplementationId();

            if (implementationId == 0) {
                // Build a city
                getCities().addCity(new City(Nation.this, tile, "Skibidiopolis"));
            }

            if (special.getKillOnUse()) {
                this.setIsDead(true);
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
            while (this.movementLeft > 0 && this.path.size() > 0) {
                this.moveToTile(this.path.remove(0));
            }
        }

        public boolean pathIsEmpty() {
            return this.path.size() == 0;
        }

        public boolean attack(Tiles.Tile tile) {
            System.out.println("Atack!");
            if (!tile.hasUnit())
                return true;

            Unit otherUnit = tile.getUnit(0);

            if (otherUnit.getIsDead())
                return true;
            if (otherUnit.getNation() == this.getNation())
                return true;
            if (otherUnit.getConfig().getDefense() == 0 && this.getConfig().getAttack() > 0) {
                // Capture unit (Mark other unit for deletion and copy to new nation)
                units.addUnit(otherUnit);
                otherUnit.setIsDead(true);
                return true;
            }
            if (otherUnit.getConfig().getDefense() + ((int) Math.random() * 2) < this.getConfig().getAttack() + ((int) Math.random() * 2))
                return true;

            setIsDead(true);
            return false;
        }

        // Moves from one point, to the next if it is valid
        public boolean moveToTile(Tiles.Tile tile) {
            int distanceToTile = distanceToTile(tile);
            if (distanceToTile != -1 && distanceToTile <= this.movementLeft) {
                this.tile.removeUnit(this);
                if (tile.getNationality() != null && tile.getNationality() != Nation.this) {
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

    static class Cities extends GameElement {
        private ArrayList<City> cities;

        public Cities() {
            this.cities = new ArrayList<City>();
        }

        public Cities(ArrayList<City> cities) {
            this.cities = cities;
        }

        public void addCity(City city) {
            cities.add(city);
        }

        public City getCity(int index) {
            return cities.get(index);
        }

        ;

        public ArrayList<City> getCities() {
            return cities;
        }

        ;

        public void nextTurn() {

        }

        public void removeCity(City c) {
            cities.remove(c);
        }

        public int size() {
            return cities.size();
        }
    }
}
