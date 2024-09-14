package game.components;

class BuildingConfig {
    private int[] implementationIds;
    private String name;

    public BuildingConfig(int[] implementationIds, String name) {
        this.implementationIds = implementationIds;
        this.name = name;
    }

    public int[] getImplementationIds() {
        return implementationIds;
    }

    public String getName() {
        return name;
    }
}
