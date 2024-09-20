package game.components;

class BuildingConfig extends Buildable {
    private int[] implementationIds;
    private String name;
    private GameThings.BuildingConfigReference prereq;

    public BuildingConfig(int[] implementationIds, String name, GameThings.BuildingConfigReference prereq, int production) {
        super(production);
        this.implementationIds = implementationIds;
        this.name = name;
        this.prereq = prereq;
    }
    public BuildingConfig(int[] implementationIds, String name, int production) {
        this(implementationIds, name, null, production);
    }

    public int[] getImplementationIds() {
        return implementationIds;
    }

    public GameThings.BuildingConfigReference getPrereq() {
        return prereq;
    }

    public String getName() {
        return name;
    }
}
