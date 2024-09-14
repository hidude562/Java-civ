package game.components;

class Yields {
    int food;
    int production;
    int science;
    int gold;
    String name;

    public Yields(String name, int food, int production, int science, int gold) {
        this.food = food;
        this.production = production;
        this.science = science;
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public int getFood() {
        return food;
    }

    public int getProduction() {
        return production;
    }

    public int getScience() {
        return science;
    }

    public int getGold() {
        return gold;
    }

    public int getTotal() {
        return food + production + science + gold;
    }

    public static Yields addYields(Yields y1, Yields y2) {
        return new Yields(y1.getName(), y1.getFood() + y2.getFood(), y1.getProduction() + y2.getProduction(), y1.getScience() + y2.getScience(), y1.getGold() + y2.getGold());
    }

    public Yields add(Yields y) {
        return Yields.addYields(this, y);
    }

    public String toString() {
        return String.format("%s: Food: %d, Production: %d, Science: %d, Gold: %d", name, food, production, science, gold);
    }
}
