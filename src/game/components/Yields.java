package game.components;

class Yields {
    int food;
    int production;
    int science;
    int gold;
    int culture;
    String name;

    public Yields(String name, int food, int production, int science, int gold, int culture) {
        this.food = food;
        this.production = production;
        this.science = science;
        this.gold = gold;
        this.culture = culture;
    }
    public Yields() {
        this.food = 0;
        this.production = 0;
        this.science = 0;
        this.gold = 0;
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

    public int getCulture() {return culture;}

    public int getTotal() {
        return food + production + science + gold + culture;
    }

    public void setName(String name) {this.name = name;}
    public void setFood(int food) {
        this.food = food;
    }
    public void setProduction(int production) {
        this.production = production;
    }
    public void setScience(int science) {
        this.science = science;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setCulture(int culture) {this.culture = culture;}

    public static Yields addYields(Yields y1, Yields y2) {
        return new Yields(y1.getName(), y1.getFood() + y2.getFood(), y1.getProduction() + y2.getProduction(), y1.getScience() + y2.getScience(), y1.getGold() + y2.getGold(), y1.getCulture() + y2.getCulture());
    }

    public Yields add(Yields y) {
        return Yields.addYields(this, y);
    }

    public String toString() {
        return String.format("%s: Food: %d, Production: %d, Science: %d, Gold: %d Culture: %d", name, food, production, science, gold, culture);
    }
}
