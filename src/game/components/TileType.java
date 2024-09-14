package game.components;

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

    abstract public void setWorked(boolean worked);

    abstract public boolean getWorked();

    abstract public Yields getYields();
    // TODO: sadboy abstract
    // abstract public void popularMonster(sadBoy _sadBoy);
}
