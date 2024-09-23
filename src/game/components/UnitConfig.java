package game.components;

// TODO: Unit kinda acts like a facade for unitconfig, so an abstract class can be used for better management
class UnitConfig extends Buildable {
    // Type of Nation.Unit
    private final String name;
    private final int startingMovement;
    private final int attack;
    private final int defense;
    private final int[] idsSpecials;
    PathfinderConfig pathfinderConfig;

    // IDs for the special
    public static final SpecialMoveConfig[] specials = {
            new SpecialMoveConfig("Build city", true, 0),
            new SpecialMoveConfig("+50% science to city", true, 1),
            new SpecialMoveConfig("+50% production to city", true, 2),
            new SpecialMoveConfig("+50% culture in city", true, 3),
            new SpecialMoveConfig("Research current tech", true, 4),
            new SpecialMoveConfig("Build current building", true, 5),
            new SpecialMoveConfig("Convert nearest city", true, 6)
    };

    public UnitConfig(String name, int startingMovement, int attack, int defense, int[] idsSpecials, PathfinderConfig pathfinderConfig, int production) {
        super(production);
        this.name = name;
        this.startingMovement = startingMovement;
        this.attack = attack;
        this.defense = defense;
        this.idsSpecials = idsSpecials;
        this.pathfinderConfig = pathfinderConfig;
    }

    public String getName() {
        return name;
    }

    public int getStartingMovement() {
        return startingMovement;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public SpecialMoveConfig[] getUnitSpecials() {
        SpecialMoveConfig[] unitSpecials = new SpecialMoveConfig[idsSpecials.length];
        for (int i = 0; i < idsSpecials.length; i++) {
            unitSpecials[i] = specials[idsSpecials[i]];
        }
        return unitSpecials;
    }

    public SpecialMoveConfig getSpecial(int id) {
        return specials[idsSpecials[id]];
    }

    public int getLengthSpecials() {
        return idsSpecials.length;
    }

    public PathfinderConfig getPathfinderConfig() {
        return pathfinderConfig;
    }
}
