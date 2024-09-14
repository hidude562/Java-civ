package game.components;

import java.util.ArrayList;

class AssortedUnitUnits {
    ArrayList<Unit> units;

    public AssortedUnitUnits() {
        units = new ArrayList<Unit>();
    }

    public void addUnit(Unit u) {
        units.add(u);
    }

    public Unit get(int i) {
        return units.get(i);
    }

    public void remove(Unit u) {
        units.remove(u);
    }

    public int size() {
        return units.size();
    }
}
