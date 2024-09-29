package game.components;

public class Culture {
    int culture = 0;
    int milestone = 1;
    Nation nation;
    public Culture(Nation nation) {
        this.nation = nation;
    }
    public int getCultureToNextMilestone() {return (milestone * milestone * 200);}
    public boolean cultureSurpassesMilestone() {return culture >= getCultureToNextMilestone();}
    public void generateGreatPerson() {
        if(nation.hasCity()) {
            nation.getCities().getCity(0).getBuilder().instantBuild(new GameThings.UnitConfigReference(1 + ((int) (Math.random() * 3))));
        }
        milestone++;
    }
    public void increaseCulture(int culture) {this.culture += culture; if(cultureSurpassesMilestone()) generateGreatPerson();}
    public int getCulture() {return culture;}
    public Nation getNation() {return nation;}
}
