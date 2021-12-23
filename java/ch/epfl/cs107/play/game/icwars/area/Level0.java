package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends ICWarsArea{

    @Override
    protected void createArea() {
        registerActor(new Background(this));
    }

    @Override
    public String getTitle() {
        return "icwars/Level0";
    }

    @Override
    public DiscreteCoordinates getAllySpawnCoordinates() {
        return new DiscreteCoordinates(0, 4);
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
