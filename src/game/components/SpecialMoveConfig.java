package game.components;

class SpecialMoveConfig {
    private final boolean killOnUse;
    private String name;
    private int implementationId;

    public SpecialMoveConfig(String name, boolean killOnUse, int implementationId) {
        this.name = name;
        this.killOnUse = killOnUse;
        this.implementationId = implementationId;
    }

    public String getName() {
        return name;
    }

    public boolean getKillOnUse() {
        return killOnUse;
    }

    public int getImplementationId() {
        return implementationId;
    }
}
