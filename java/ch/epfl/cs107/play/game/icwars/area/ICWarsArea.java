package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2Behavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public abstract class ICWarsArea extends Area {

    private ICWarsBehavior behavior ;
    private ArrayList<Unit> unitList ;

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();
    public abstract DiscreteCoordinates getAllySpawnCoordinates();
    public abstract DiscreteCoordinates getEnemySpawnCoordinates();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            behavior = new ICWarsBehavior(window, getTitle());
            unitList = new ArrayList<Unit>();
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    @Override
    public final float getCameraScaleFactor() {
        return ICWars.CAMERA_SCALE_FACTOR;
    }
}
