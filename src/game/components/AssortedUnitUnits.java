package game.components;

import java.util.ArrayList;

class AssortedUnitUnits {
    ArrayList<Nation.Unit> units;

    public AssortedUnitUnits() {
        units = new ArrayList<Nation.Unit>();
    }

    public void addUnit(Nation.Unit u) {
        units.add(u);
    }

    public Nation.Unit get(int i) {
        return units.get(i);
    }

    public void remove(Nation.Unit u) {
        units.remove(u);
    }

    public int size() {
        return units.size();
    }
}
