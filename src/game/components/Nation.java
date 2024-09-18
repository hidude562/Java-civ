package game.components;

import java.util.ArrayList;

// A nation
class Nation extends GameElement {
    // 0 -> Alexander the Great, 1 -> G. Washington, etc. Each have bonuses and allat.
    private int nation;
    private Cities cities;
    private Units units;
    private TechTree techTree;

    public Nation(int nation) {
        this.nation = nation;
        this.cities = new Cities();
        this.units = new Units();
        this.techTree = new TechTree();
    }

    public Cities getCities() {
        return cities;
    }

    public void removeCity(City c) {
        cities.removeCity(c);
    }

    public void addUnit(Unit u) {
        units.addUnit(u);
    }

    public Units getUnits() {
        return units;
    }

    public void techTreeManage() {
        if(!techTree.isResearching()) {
            ArrayList<TechTree.Technology> potentialTechs = techTree.getTechsCanResearch();
            if(potentialTechs.size() > 0)
                techTree.setTechResearch(potentialTechs.get((int) (Math.random() * potentialTechs.size())));
        }
        System.out.println(techTree);
    }

    public void nextTurn() {
        Yields citiesYields = cities.getTotalYields();
        techTree.increaseScienceProgress(citiesYields.getScience());
        techTreeManage();

        cities.nextTurn();
        units.nextTurn();
    }

    public TechTree getTechTree() {
        return techTree;
    }

    class Units extends GameElement {
        private ArrayList<Unit> units;

        public Units() {
            this.units = new ArrayList<Unit>();
        }

        public void addUnit(Unit unit) {
            units.add(unit);
        }

        public Unit getUnit(int index) {
            return units.get(index);
        }

        public ArrayList<Unit> getUnits() {
            return units;
        }

        public void nextTurn() {
            for (int i = 0; i < units.size(); i++) {
                Unit u = units.get(i);
                if (u.getIsDead()) {
                    units.remove(i);
                    i--;
                }
                u.nextTurn();
            }
        }

        public int size() {
            return units.size();
        }
    }

    static class Cities extends GameElement {
        private ArrayList<City> cities;

        public Cities() {
            this.cities = new ArrayList<City>();
        }

        public Cities(ArrayList<City> cities) {
            this.cities = cities;
        }

        public void addCity(City city) {
            cities.add(city);
        }

        public City getCity(int index) {
            return cities.get(index);
        }

        public ArrayList<City> getCities() {
            return cities;
        }

        public Yields getTotalYields() {
            Yields yields = new Yields("",0,0,0,0);
            for(City c : cities) {
                yields = yields.add(c.getYields());
            }
            return yields;
        }

        public void nextTurn() {
            for (int i = 0; i < cities.size(); i++) {
                City c = cities.get(i);
                c.nextTurn();
            }
        }

        public void removeCity(City c) {
            cities.remove(c);
        }

        public int size() {
            return cities.size();
        }
    }
}
