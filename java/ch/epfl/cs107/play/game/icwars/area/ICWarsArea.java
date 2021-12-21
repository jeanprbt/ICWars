package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.city.ICWarsCity;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2Behavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class ICWarsArea extends Area {

    private ICWarsBehavior behavior;
    private ArrayList<Unit> unitList;

    //-----------------------------------API-------------------------------------//
    public abstract DiscreteCoordinates getAllySpawnCoordinates();
    public abstract DiscreteCoordinates getEnemySpawnCoordinates();


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            behavior = new ICWarsBehavior(window, getTitle(), this);
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

    /**
     * Method returning all ally cities given a faction
     * @param faction : the ally faction for cities
     */
    public ArrayList<ICWarsCity> getCities(ICWarsActor.Faction faction){
        ArrayList<ICWarsCity> cities = new ArrayList<ICWarsCity>();
        for (ICWarsCity city : behavior.getCities()) {
            if(city.getFaction() == faction){
                cities.add(city);
            }
        }
        return cities ;
    }


    /**
     * Method returning all enemies given a faction
     * @param faction : the faction of the unit for which we want to know its enemies
     */
    public ArrayList<Unit> getEnemies(ICWarsActor.Faction faction) {
        ArrayList<Unit> enemies = new ArrayList<Unit>();
        for (Unit unit : unitList) {
            if (unit.getFaction() == faction) continue;
            enemies.add(unit);
        }
        return enemies;
    }

    /**
     * Method returning all enemies position given a selectedUnit (knowing its faction this way)
     * @param selectedUnit : the unit for which we want to know its enemies positions
     */
    public ArrayList<DiscreteCoordinates> getEnemiesPosition(Unit selectedUnit) {
        ArrayList<DiscreteCoordinates> enemiesPosition = new ArrayList<DiscreteCoordinates>();
        for (Unit enemy : getEnemies(selectedUnit.getFaction())) {
            enemiesPosition.add(new DiscreteCoordinates((int) enemy.getPosition().x, (int) enemy.getPosition().y));
        }
        return enemiesPosition;
    }

    /**
     * Method returning the closest enemy position given a selectedUnit (we use an ArrayList
     * of all enemies positions and calculate their euclidian distance to the selectedUnit in
     * order to determine the min distance and therefore the closest enemy)
     * @param selectedUnit
     */
    public DiscreteCoordinates getClosestEnemyPosition(Unit selectedUnit) {
        ArrayList<DiscreteCoordinates> coordsToTest = getEnemiesPosition(selectedUnit);
        DiscreteCoordinates closestEnemyPosition = null;
        DiscreteCoordinates selectedUnitPosition = new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int) selectedUnit.getPosition().y);
        double minDistance = getWidth();
        for (DiscreteCoordinates coordinates : coordsToTest) {
            double distance = Math.sqrt(Math.pow(coordinates.x - selectedUnitPosition.x, 2) + Math.pow(coordinates.y - selectedUnitPosition.y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemyPosition = coordinates;
            }
        }
        return closestEnemyPosition;
    }


    /**
     * Little method aiming at adding the unit in parameter to unitList
     * used in the method enterArea() of ICWarsPlayer for all effectives
     */
    public void addToUnitList(Unit unit) {
        unitList.add(unit);
    }

    /**
     * Little method at aiming at removing a unit when it's dead,
     * called in controlUnits() method of ICWarsPlayer.java
     *
     * @param unit : unit to remove
     */
    public void removeFromUnitList(Unit unit) {
        unitList.remove(unit);
    }

    /**
     * Litte method aiming at removing everything from unitList when charging
     * a new area, called in method changeArea() of ICWars.java
     */
    public void clearUnitList() {
        unitList.clear();
    }

    //-----------------------------------Protected-------------------------------------//

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();
}
