package game.components;

import java.util.ArrayList;

class GameThings {
    public static final UnitConfig[] units = new UnitConfig[] {
            new UnitConfig("Settler", 2, 0, 0, new int[]{0}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Warrior", 1, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Guy on a horse", 2, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),

            new UnitConfig("Archer", 1, 1, 2, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Legion", 1, 2, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Chariot", 2, 2, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),

            new UnitConfig("Crossbow man", 1, 2, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Catapult", 1, 3, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Knight", 2, 4, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),

            new UnitConfig("Riflemen", 1, 3, 5, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Cannon", 1, 6, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Armored Car", 3, 6, 6, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),

            new UnitConfig("Infantry", 1, 7, 7, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Artillery", 1, 9, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Tank", 3, 9, 9, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),

            new UnitConfig("Mechanized Infantry", 3, 10, 10, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Rocket Artillery", 3, 12, 5, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
            new UnitConfig("Modern Armor", 4, 12, 10, new int[]{}, new PathfinderConfig(true, new int[]{0, 1})),
    };
    public static final BuildingConfig[] buildings = new BuildingConfig[] {
            new BuildingConfig(new int[]{0}, "Library"),
            new BuildingConfig(new int[]{1}, "Market"),
    };
    public static final Government[] governments = new Government[]{
            new Government("Despotism"),
            new Government("Republic"),
            new Government("Democracy"),
            new Government("Monarchy"),
            new Government("Communist"),
            new Government("Fascist"),
            new Government("Technocracy"),
    };

    abstract public class Reference {
        int reference;
        public Reference(int reference) {
            this.reference = reference;
        }
        abstract public Object get();
        public int getReference() {return reference;}
    }

    public class UnitConfigReference extends Reference {
        public UnitConfigReference(int reference) {
            super(reference);
        }
        public Object get() {return units[reference];}
    }
    public class BuildingConfigReference extends Reference {
        public BuildingConfigReference(int reference) {
            super(reference);
        }
        public Object get() {return buildings[reference];}
    }
    public class GovernmentConfigReference extends Reference {
        public GovernmentConfigReference(int reference) {
            super(reference);
        }
        public Object get() {return governments[reference];}
    }

    private ArrayList<UnitConfigReference> unitsRefs;
    private ArrayList<BuildingConfigReference> buildingsRefs;
    private ArrayList<GovernmentConfigReference> governmentsRefs;

    public GameThings() {
        this.unitsRefs = new ArrayList<UnitConfigReference>();
        this.buildingsRefs = new ArrayList<BuildingConfigReference>();
        this.governmentsRefs = new ArrayList<GovernmentConfigReference>();
    }
    public void unlockUnit(int id)       {this.unitsRefs.add(new UnitConfigReference(id));}
    public void unlockBuilding(int id)   {this.buildingsRefs.add(new BuildingConfigReference(id));}
    public void unlockGovernment(int id) {this.governmentsRefs.add(new GovernmentConfigReference(id));}

    public ArrayList<UnitConfigReference> getUnitsRefs() {return unitsRefs;}
    public ArrayList<BuildingConfigReference> getBuildingRefs() {return buildingsRefs;}
    public ArrayList<GovernmentConfigReference> getGovernmentRefs() {return governmentsRefs;}


}
