package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level1 extends ICWarsArea {

    //-----------------------------------API-------------------------------------//


    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    @Override
    public DiscreteCoordinates getAllySpawnCoordinates() {
        return new DiscreteCoordinates(2, 5);
    }

    @Override
    public DiscreteCoordinates getEnemySpawnCoordinates() {
        return new DiscreteCoordinates(8, 5);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
    }

}
