package game.components;

import java.util.ArrayList;

class GameThings {
    public static final UnitConfig[] units = new UnitConfig[] {
            new UnitConfig("Settler", 2, 0, 0, new int[]{0}, new PathfinderConfig(true, new int[]{0, 1}), 30),
            new UnitConfig("Militia", 1, 0, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 5),
            new UnitConfig("Warrior", 1, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 10),
            new UnitConfig("Guy on a horse", 2, 1, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 20),

            new UnitConfig("Archer", 1, 1, 2, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 15),
            new UnitConfig("Legion", 1, 2, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 20),
            new UnitConfig("Chariot", 2, 2, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 25),

            new UnitConfig("Crossbow man", 1, 2, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 20),
            new UnitConfig("Catapult", 1, 3, 1, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 20),
            new UnitConfig("Knight", 2, 4, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 30),

            new UnitConfig("Riflemen", 1, 3, 5, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 30),
            new UnitConfig("Cannon", 1, 6, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 30),
            new UnitConfig("Armored Car", 3, 6, 6, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 40),

            new UnitConfig("Infantry", 1, 7, 7, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 35),
            new UnitConfig("Artillery", 1, 9, 3, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 35),
            new UnitConfig("Tank", 3, 9, 9, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 45),

            new UnitConfig("Mechanized Infantry", 3, 10, 10, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 40),
            new UnitConfig("Rocket Artillery", 3, 12, 5, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 40),
            new UnitConfig("Modern Armor", 4, 12, 10, new int[]{}, new PathfinderConfig(true, new int[]{0, 1}), 50),
    };
    public static final BuildingConfig[] buildings = new BuildingConfig[] {
            new BuildingConfig(new int[]{0}, "Library", 40),
            new BuildingConfig(new int[]{1}, "University", new BuildingConfigReference(0), 80),
            new BuildingConfig(new int[]{2}, "Market", 30),
            new BuildingConfig(new int[]{3}, "Bank", new BuildingConfigReference(2), 60),
            new BuildingConfig(new int[]{4}, "Aqueduct", 50),
            new BuildingConfig(new int[]{5}, "Ampitheatre", 30),
            new BuildingConfig(new int[]{6}, "Museum", new BuildingConfigReference(5), 60),
            new BuildingConfig(new int[]{7}, "Workshop", 30),
            new BuildingConfig(new int[]{8}, "Courthouse", 80),
            new BuildingConfig(new int[]{9}, "Sewers", 60),
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

    abstract public static class Reference {
        int reference;
        public Reference(int reference) {
            this.reference = reference;
        }
        abstract public Object get();
        public int getReference() {return reference;}
    }

    public static class UnitConfigReference extends Reference {
        public UnitConfigReference(int reference) {
            super(reference);
        }
        public Object get() {return units[reference];}
    }
    public static class BuildingConfigReference extends Reference {
        public BuildingConfigReference(int reference) {
            super(reference);
        }
        public Object get() {return buildings[reference];}
    }
    public static class GovernmentConfigReference extends Reference {
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

        // Unlock default things
        this.unitsRefs.add(new UnitConfigReference(0));
        this.unitsRefs.add(new UnitConfigReference(1));
        this.unitsRefs.add(new UnitConfigReference(2));
    }
    public void unlockUnit(UnitConfigReference u) {this.unitsRefs.add(u);}
    public void unlockBuilding(BuildingConfigReference b) {this.buildingsRefs.add(b);}
    public void unlockGovernment(GovernmentConfigReference g) {this.governmentsRefs.add(g);}

    public void unlock(Reference r) {
        if(r instanceof UnitConfigReference) {
            unlockUnit((UnitConfigReference) r);
        } else if(r instanceof BuildingConfigReference) {
            unlockBuilding((BuildingConfigReference) r);
        } else if(r instanceof GovernmentConfigReference) {
            unlockGovernment((GovernmentConfigReference) r);
        }
    }

    public ArrayList<UnitConfigReference> getUnitsRefs() {return unitsRefs;}
    public ArrayList<BuildingConfigReference> getBuildingRefs() {return buildingsRefs;}
    public ArrayList<GovernmentConfigReference> getGovernmentRefs() {return governmentsRefs;}
}
