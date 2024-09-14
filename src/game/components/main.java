package game.components;

public class main {
    public static void main(String[] args) {
        World world = new World(new Vert2D(
                32, 32
        ));
        System.out.println(world);
        Nation playerNation = world.getNations().getNation(0);
        {
            Unit settler = playerNation.getUnits().getUnit(0);

            while (true) {
                settler.setPath(
                        world.getTiles().getTile(world.getTiles().getIndexFromPoint(
                                new Vert2D(12, 12)
                        ))
                );
                System.out.println(world);
                world.nextTurn();
                if (settler.pathIsEmpty()) break;
            }
            System.out.println(world);
            settler.unitAction(0);
            System.out.println(world);
        }

        City userCity = playerNation.getCities().getCity(0);
        System.out.println(userCity.toString());
        userCity.setProduction(new Unit(playerNation, 0));

        for(int t = 0; t < 150; t++) {
            world.nextTurn();
            System.out.println(world);
        }
    }
}