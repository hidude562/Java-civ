package game.components;


import java.util.ArrayList;

class TechTree {
    private static class TechnologyData {
        // Ids for the things that it unlocks? (TODO: Maybe not best structure)
        int[] gameElementsUnlocked;
        int[] idTechnologyPrereqs;
        String name;

        public TechnologyData(String name, int[] idTechnologyPrereqs, int[] gameElementsUnlocked) {
            this.name = name;
            this.idTechnologyPrereqs = idTechnologyPrereqs;
            this.gameElementsUnlocked = gameElementsUnlocked;
        }
        public TechnologyData(String name, int[] idTechnologyPrereqs) {
            this.name = name;
            this.idTechnologyPrereqs = idTechnologyPrereqs;
        }
        public String getName() {return name;}
        public int[] getIdTechnologyPrereqs() {return idTechnologyPrereqs;}
        public int[] getGameElementsUnlocked() {return gameElementsUnlocked;}
    }
    public static final TechnologyData[] techTree = {
            new TechnologyData("Pottery", new int[0]),
            new TechnologyData("Bronze working", new int[0]),
            new TechnologyData("Alphabet", new int[0]),
            new TechnologyData("Agriculture", new int[0]),

            new TechnologyData("Wheel", new int[]{0}),
            new TechnologyData("Mining", new int[]{1}),
            new TechnologyData("Writing", new int[]{2}),
            new TechnologyData("Irrigation", new int[]{3}),

            new TechnologyData("Trade", new int[]{4, 6}),
            new TechnologyData("Engineering", new int[]{5}),
            new TechnologyData("Literacy", new int[]{6}),
            new TechnologyData("Art", new int[]{0, 6}),

            new TechnologyData("Banking", new int[]{8}),
            new TechnologyData("Chivalry", new int[]{9, 7}),
            new TechnologyData("Education", new int[]{10}),
            new TechnologyData("Divine right", new int[]{10, 11}),

            new TechnologyData("Urbanization", new int[]{12}),
            new TechnologyData("Invention", new int[]{12, 14}),
            new TechnologyData("Colonialism", new int[]{12}),
            new TechnologyData("Gunpowder", new int[]{14}),

            new TechnologyData("Industrialization", new int[]{16, 17}),
            new TechnologyData("Explosives", new int[]{19}),
            new TechnologyData("Locomotion", new int[]{18}),
            new TechnologyData("Nationalism", new int[]{18}),

            new TechnologyData("Communism", new int[]{20, 23}),
            new TechnologyData("Combustion", new int[]{21, 22}),
            new TechnologyData("Artillery", new int[]{21, 22}),
            new TechnologyData("Mass production", new int[]{19, 20}),

            new TechnologyData("Chemistry", new int[]{27}),
            new TechnologyData("Nuclear Fission", new int[]{25, 26}),
            new TechnologyData("Flight", new int[]{25}),
            new TechnologyData("Computers", new int[]{27}),

            new TechnologyData("Guidance systems", new int[]{28, 30}),
            new TechnologyData("Modern armor", new int[]{25, 28}),
            new TechnologyData("Advanced flight", new int[]{30}),
            new TechnologyData("Mechanized Infantry", new int[]{31}),

            new TechnologyData("Nanotechnology", new int[]{32}),
            new TechnologyData("Nuclear fusion", new int[]{29}),
            new TechnologyData("Space exploration", new int[]{29, 31, 34}),
            new TechnologyData("Artificial Intelligence", new int[]{35}),
    };
    public class Technology {
        private int referenceIndex;
        private boolean isResearched;
        public Technology(int referenceIndex) {
            this.referenceIndex = referenceIndex;
            this.isResearched = false;
        }
        public Technology(int referenceIndex, boolean isResearched) {
            this.referenceIndex = referenceIndex;
            this.isResearched = isResearched;
        }
        private TechnologyData technologyData() {return techTree[referenceIndex];}
        public String getName() {return technologyData().getName();}
        public Technology[] getTechnologyPrereqs() {
            int[] idPrereqs = technologyData().getIdTechnologyPrereqs();
            Technology[] prereqs = new Technology[idPrereqs.length];
            for(int i=0;i<idPrereqs.length;i++) {
                prereqs[i] = new Technology(idPrereqs[i]);
            }
            return prereqs;
        }
        public int[] getGameElementsUnlocked() {return technologyData().getGameElementsUnlocked();}

        public void setResearched(boolean b) {
            this.isResearched = b;
        }

        public boolean isResearched() {
            return isResearched;
        }
    }

    private Technology[] techs;
    private int scienceProgress;
    private int researchingTech;
    public TechTree() {
        this.techs = new Technology[techTree.length];
        for(int i = 0; i < techs.length; i++)
            this.techs[i] = new Technology(i, false);
        this.scienceProgress = 0;
        this.researchingTech = -1;
    }
    public int getScienceToCompleteTechnology() {
        if(researchingTech == -1)
            return 0;
        int techLevel = (researchingTech/4);
        return (techLevel * techLevel * 15) + 20;
    }
    public boolean currentTechResearchIsCompleted() {
        return scienceProgress >= getScienceToCompleteTechnology();
    }
    public void finishTechIfFinished() {
        if(this.researchingTech != -1 && currentTechResearchIsCompleted()) {
            techs[this.researchingTech].setResearched(true);
        }
    }
    public void increaseScienceProgress(int scienceProgress) {
        this.scienceProgress += scienceProgress;
        finishTechIfFinished();
    }
    public boolean isResearched(int id) {
        return techs[id].isResearched();
    }
    public Technology getTechnology(int id) {
        return techs[id];
    }
    public boolean canResearchTech(int id) {
        if(isResearched(id))
            return false;

        Technology t = getTechnology(id);
        for(int i = 0; i < t.getTechnologyPrereqs().length; i++) {
            if(!t.getTechnologyPrereqs()[i].isResearched())
                return false;
        }
        return true;
    }
    public ArrayList<Technology> getTechsCanResearch() {
        // TODO: More efficient
        ArrayList<Technology> canResearch = new ArrayList<Technology>();
        for(int i = 0; i < techTree.length; i++) {
            if(!isResearched(i)) {
                if(canResearchTech(i)) {
                    canResearch.add(getTechnology(i));
                }
            }
        }
        return canResearch;
    }
    public void setTechResearch(int id) {
        if(canResearchTech(id))
        this.researchingTech = id;
    }
    public Technology getTech() {
        return techs[this.researchingTech];
    }
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for(int y = 0; y < 4; y++) {
            for(int x = y; x < techTree.length; x+=4) {
                String techStr = getTechnology(x).getName() + (isResearched(x) ? "✔" : "✘");
                techStr = String.format("%-20s",techStr);
                str.append(techStr);
            }
            str.append("\n");
        }
        return str.toString();
    }
}