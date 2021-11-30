package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends ICWarsArea{

    @Override
    protected void createArea() {

    }

    @Override
    public String getTitle() {
        return "icwars/Level0";
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnCoordinates() {
        return new DiscreteCoordinates(0, 0);
    }
}
