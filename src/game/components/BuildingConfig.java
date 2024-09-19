package game.components;

class BuildingConfig {
    private int[] implementationIds;
    private String name;
    private GameThings.BuildingConfigReference prereq;

    public BuildingConfig(int[] implementationIds, String name, GameThings.BuildingConfigReference prereq) {
        this.implementationIds = implementationIds;
        this.name = name;
        this.prereq = prereq;
    }
    public BuildingConfig(int[] implementationIds, String name) {
        this(implementationIds, name, null);
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
