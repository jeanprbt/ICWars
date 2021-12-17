package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import java.util.ArrayList;

public class AIPlayer extends ICWarsPlayer {

    private boolean counting ;
    private float counter;
    private Sprite sprite ;
    private DiscreteCoordinates selectedUnitPosition ;

    //List of units waiting for current round
    private ArrayList<Unit> currentRoundUnits ;

    public AIPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units){
        super(area, position, faction, units);
        counting = true ;
        counter = 0f ;
        sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
        currentRoundUnits = new ArrayList<Unit>();
        fillEffectiveList();
    }

    /**
     * Method to update player's state if certain conditions are met
     * The method is called by the update method of AIPlayer.java
     */
    private void updatePlayerState(){
        switch (getCurrentPlayerState()){
            case IDLE:
                break;
            case NORMAL:
                centerCamera();
                if(currentRoundUnits.size() == 0) setCurrentPlayerState(ICWarsPlayerState.IDLE);
                else {
                    while (!waitFor(1000000000, 1f)){break;}
                    System.out.println("p");
                    selectedUnit = currentRoundUnits.get(0);
                    selectedUnitPosition = new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int)selectedUnit.getPosition().y);
                    currentRoundUnits.remove(selectedUnit);
                    changePosition(new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int) selectedUnit.getPosition().y));
                    setCurrentPlayerState(ICWarsPlayerState.SELECT_CELL);
                }
                break;
            case SELECT_CELL:
                setCurrentPlayerState(ICWarsPlayerState.MOVE_UNIT);
                break ;
            case MOVE_UNIT:
                System.out.println(selectedUnit);
                changePosition(dondeVamos());
                selectedUnit.changePosition(dondeVamos());
                setCurrentPlayerState(ICWarsPlayerState.NORMAL);
                break;
            case ACTION_SELECTION:
            case ACTION:
            default:
                break;
        }
    }

    @Override
    public void update(float deltaTime) {
        updatePlayerState();
        super.update(deltaTime);
    }

    /**
     * Ensures that value time elapsed before returning true
     * @param dt elapsed time
     * @param value waiting time (in seconds )
     * @return true if value seconds has elapsed , false otherwise
     */
    private boolean waitFor (float value , float dt) {
        if (counting) {
            counter += dt;
            if (counter > value ) {
                counting = false ;
                return true;
            }
        } else {
            counter = 0f;
            counting = true;
        }
        return false ;
    }

    /**
     * Method to call when needing to fill the effective list of AIPlayer waiting
     * for current round (which have not been playe yet).
     */
    private void fillEffectiveList(){
        for (int i = 0; i < getEffectivesSize(); i++) {
            currentRoundUnits.add(getUnitFromIndex(i));
        }
    }

    /**
     * Method to check if a given x is in the x-range of selectedUnit
     * @param positionX positionX to check
     */
    private boolean isInRangeX(int positionX) {
        if(Math.abs(selectedUnit.getPosition().x - positionX) <= selectedUnit.getRadius()) return true;
        return false;
    }

    /**
     * Method to check if a given y is in the y-range of selectedUnit
     * @param positionY positionY to check
     */
    private boolean isInRangeY(int positionY) {
        if(Math.abs(selectedUnit.getPosition().y - positionY) <= selectedUnit.getRadius()) return true ;
        return false ;
    }

    /**
     * Method that returns the optimal position for selectedUnit to move to
     * in step MOVE_UNIT
     * @return optimal position for selectedUnit in step MOVE_UNIT
     */
    private DiscreteCoordinates dondeVamos(){
        DiscreteCoordinates finalPosition ;
        Unit closestUnit = getClosestUnit();
        DiscreteCoordinates closestUnitPosition = new DiscreteCoordinates((int)closestUnit.getPosition().x, (int)closestUnit.getPosition().y);
        finalPosition = getOptimalRangeBorders(closestUnitPosition);
        return finalPosition;
    }

    /**
     * Method returning the position where selectedUnit should move in order to get as close
     * as possible from the closest enemy unit
     * @param closestUnitPosition : targeted unit's position
     */
    private DiscreteCoordinates getOptimalRangeBorders(DiscreteCoordinates closestUnitPosition){
        DiscreteCoordinates optimalRangeBorders;
        int finalX = selectedUnitPosition.x;
        int finalY = selectedUnitPosition.y;

        // Compare the x's
        if (isInRangeX(closestUnitPosition.x)) finalX = closestUnitPosition.x;
        else {
            int differenceX = selectedUnitPosition.x - closestUnitPosition.x;
            if (differenceX < 0) {
                finalX = selectedUnitPosition.x + selectedUnit.getRadius();
            } else if (differenceX > 0) {
                finalX = selectedUnitPosition.x - selectedUnit.getRadius();
            }
        }

        // Compare the y's
        if(isInRangeY(closestUnitPosition.y)) finalY = closestUnitPosition.y;
        else {
            int differenceY = selectedUnitPosition.y - closestUnitPosition.y;
            if (differenceY < 0) {
                finalY = selectedUnitPosition.y + selectedUnit.getRadius();
            } else if (differenceY > 0) {
                finalY = selectedUnitPosition.y - selectedUnit.getRadius();
            }
        }

        optimalRangeBorders = new DiscreteCoordinates(finalX, finalY);
        return optimalRangeBorders ;
    }

    /**
     * Method returning indexes of all enemy units on area
     * @return the list of targets' indexes in ICWarsArea's unitList
     */
    private ArrayList<Integer> findTargetsIndexes() {
        ArrayList<Integer> targetsIndexes = new ArrayList<Integer>();
        ICWarsArea area = (ICWarsArea) getOwnerArea() ;
        for (int i = 0; i < area.getUnitListSize(); i++) {
            Unit unit = area.getUnitFromIndex(i);
            if(unit.getFaction() == this.getFaction()) continue ;
            targetsIndexes.add(area.getIndexInUnitList(unit));
        }
        return targetsIndexes ;
    }

    /**
     * Method determining the closest enemy unit to the
     * current selectedUnit.
     */
    private Unit getClosestUnit(){
        ArrayList<Unit> unitsToTest = new ArrayList<Unit>();
        ArrayList<Integer> targetIndexes = findTargetsIndexes() ;
        for (Integer targetIndex : targetIndexes) {
            Unit unit = getUnitFromIndex(targetIndex);
            unitsToTest.add(unit);
        }
        Unit closestUnit = compareUnitsPositions(unitsToTest);
        return closestUnit;
    }


    /**
     * Method returning the unit among a list whose position is closest to selectedUnit
     * @param units : ArrayList of units to be checked
     */
    private Unit compareUnitsPositions(ArrayList<Unit> units) {
        Unit closestUnit = null ;
        //huge value - to be changed
        double minEuclidianDistance = getOwnerArea().getWidth() ;
        for (Unit unit : units) {
            DiscreteCoordinates targetPosition = new DiscreteCoordinates((int)unit.getPosition().x, (int)unit.getPosition().y);
            double euclidianDistance = Math.sqrt(Math.pow(selectedUnitPosition.x - targetPosition.x, 2) + Math.pow(selectedUnitPosition.y - targetPosition.y, 2));
            if(euclidianDistance < minEuclidianDistance) {
                minEuclidianDistance = euclidianDistance ;
                closestUnit = unit ;
            }
        }
        return closestUnit;
    }

    @Override
    public void draw(Canvas canvas) {
        if(getCurrentPlayerState() != ICWarsPlayerState.IDLE) sprite.draw(canvas);
    }
}

