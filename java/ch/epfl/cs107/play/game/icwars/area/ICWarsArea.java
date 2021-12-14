package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2Behavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public abstract class ICWarsArea extends Area {

    private ICWarsBehavior behavior ;
    public ArrayList<Unit> unitList ;

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();
    public abstract DiscreteCoordinates getAllySpawnCoordinates();
    public abstract DiscreteCoordinates getEnemySpawnCoordinates();

    /**
     * Little method aiming at adding the unit in parameter to unitList
     * used in the method enterArea() of ICWarsPlayer for all effectives
     */
    public void addToUnitList(Unit unit){
        unitList.add(unit);
        System.out.println(unitList);
    }

    /**
     * Litte method aiming at removing everything from unitList when charging
     * a new area, called in method changeArea() of ICWars.java
     */
    public void clearUnitList(){
        unitList.clear();
    }

    /**
     * Little method at aiming at removing a unit when it's dead,
     * called in controlUnits() method of ICWarsPlayer.java
     * @param unit : unit to remove
     */
    public void removeFromUnitList(Unit unit){
        unitList.remove(unit);
    }

    public int getIndexInUnitList(Unit unit){
        return unitList.indexOf(unit);
    }

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
