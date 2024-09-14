package game.components;

// The data stored for the tiles
// Don't use this for getting tile data, use Tiles.tile
class TileData extends TileType {
    private int type;
    public static final Yields[] tileYields = new Yields[]{
            new Yields("Ocean", 1, 0, 0, 0),
            new Yields("Coast", 1, 0, 1, 0),
            new Yields("Ice", 0, 0, 0, 0),
            new Yields("Tundra", 1, 0, 0, 0),
            new Yields("Grassland", 2, 0, 0, 0),
            new Yields("Plains", 1, 1, 0, 0),
            new Yields("Desert", 0, 0, 0, 1)
    };
    private AssortedUnitUnits units;
    private City cityCenter = null;
    private City ownedCity = null;
    private boolean worked = false;

    public TileData() {
        units = new AssortedUnitUnits();
    }

    public TileData(int type) {
        this.type = type;
    }

    private TileData tileData() {
        return this;
    }

    public int getType() {
        return type;
    }

    public AssortedUnitUnits getUnits() {
        return units;
    }

    public String toString() {
        return String.valueOf(getType());
    }

    public void setType(int type) {
        this.type = type;
    }

    public void addUnit(Unit unit) {
        units.addUnit(unit);
    }

    public boolean hasUnit() {
        return units.size() > 0;
    }

    public Unit getUnit(int index) {
        return units.get(index);
    }

    public void removeUnit(Unit unit) {
        units.units.remove(unit);
    }

    public void setCityCenter(City city) {
        this.cityCenter = city;
    }

    public City getCityCenter() {
        return cityCenter;
    }

    public boolean hasCityCenter() {
        return cityCenter != null;
    }

    public void setOwnedCity(City ownedCity) {
        this.ownedCity = ownedCity;
    }

    public City getOwnedCity() {
        return this.ownedCity;
    }

    public boolean hasOwnedCity() {
        return this.ownedCity != null;
    }

    public void setWorked(boolean worked) {
        this.worked = worked;
    }

    public boolean getWorked() {
        return worked;
    }

    public Yields getYields() {
        return tileYields[getType()];
    }

    public Nation getNationality() {
        for(int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            if(!u.getIsDead()) {
                return u.getNation();
            }
        }
        return null;
    }
}
