package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.city.ICWarsCity;
import ch.epfl.cs107.play.game.icwars.actor.unit.RocketMan;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.*;
import ch.epfl.cs107.play.game.icwars.exception.WrongLocationException;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends ICWarsPlayer {

    private Sprite sprite ;
    private DiscreteCoordinates selectedUnitPosition ;
    private ICWarsAction actionToExecute ;
    private int counter;
    private ArrayList<Unit> aiEffectives;
    private ICWarsArea area ;

    //-----------------------------------API-------------------------------------//

    public AIPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units){
        super(area, position, faction, units);
        sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
        counter = 0;
        aiEffectives = new ArrayList<Unit>();
        fillEffectiveList();
    }

    @Override
    public void update(float deltaTime) {
        updatePlayerState();
        controlUnits();
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if(getCurrentPlayerState() != ICWarsPlayerState.IDLE) sprite.draw(canvas);
        if(getCurrentPlayerState() == ICWarsPlayerState.ACTION) actionToExecute.draw(canvas);
    }

    //-----------------------------------Private-------------------------------------//

    /**
     * Method to update player's state if certain conditions are met
     * The method is called by the update method of AIPlayer.java
     */
    private void updatePlayerState(){
        switch (getCurrentPlayerState()){
            case IDLE:
                fillEffectiveList();
                actionToExecute = null ;
                break;
            case NORMAL:
                area = (ICWarsArea) getOwnerArea() ;
                if(area.getEnemies(getFaction()).size() == 0) {
                    setCurrentPlayerState(ICWarsPlayerState.IDLE);
                    break;
                }
                centerCamera();
                if(haveBeenUsed(aiEffectives)) {
                    setCurrentPlayerState(ICWarsPlayerState.IDLE);
                }
                else {
                    selectedUnit = aiEffectives.get(counter);
                    selectedUnitPosition = new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int)selectedUnit.getPosition().y);
                    waitFor(300);
                    changePosition(selectedUnitPosition);
                    waitFor(300);
                    setCurrentPlayerState(ICWarsPlayerState.MOVE_UNIT);
                }
                ++counter;
                break ;
                case MOVE_UNIT:
                    waitFor(300);
                    if(selectedUnit instanceof Tank){
                        List<ICWarsCity>foreignCities = area.getCities(getFaction(), true);
                        if (foreignCities != null){
                            for (ICWarsCity foreignCity : foreignCities) {
                                DiscreteCoordinates cityPosition = new DiscreteCoordinates((int) foreignCity.getPosition().x, (int) foreignCity.getPosition().y);
                                if(isInRange(cityPosition, selectedUnit)) {
                                    changePosition(cityPosition);
                                    selectedUnit.setHasBeenUsed(true);
                                    waitFor(300);
                                    selectedUnit.changePosition(cityPosition);
                                    waitFor(300);
                                    setCurrentPlayerState(ICWarsPlayerState.ACTION_SELECTION);
                                    break;
                                }
                            }
                            if(getCurrentPlayerState() == ICWarsPlayerState.ACTION_SELECTION){
                                break ;
                            }
                            else {
                                changePosition(getClosestPositionPossible());
                                selectedUnit.setHasBeenUsed(true);
                                waitFor(300);
                                selectedUnit.changePosition(getClosestPositionPossible());
                                waitFor(300);
                                setCurrentPlayerState(ICWarsPlayerState.ACTION_SELECTION);
                                break;
                            }
                        }
                    }
                    changePosition(getClosestPositionPossible());
                    selectedUnit.setHasBeenUsed(true);
                    waitFor(300);
                    selectedUnit.changePosition(getClosestPositionPossible());
                    waitFor(300);
                    setCurrentPlayerState(ICWarsPlayerState.ACTION_SELECTION);
                    break;
                case ACTION_SELECTION:
                    ArrayList<Unit> targets = new ArrayList<Unit>();
                    if(selectedUnit instanceof Tank){
                        for (ICWarsCity city : area.getCities(getFaction(), true)) {
                            if(selectedUnit.getPosition().equals(city.getPosition())){
                                actionToExecute = new CaptureAction(area, selectedUnit);
                                setCurrentPlayerState(ICWarsPlayerState.ACTION);
                            }
                        }
                       if(actionToExecute == null){}
                       else break ;
                    }
                    if(selectedUnit instanceof RocketMan){
                        for (Unit enemy : area.getEnemies(this.getFaction())) {
                            targets.add(enemy);
                        }
                        actionToExecute = new RocketManAttackAction(area, selectedUnit);
                        setCurrentPlayerState(ICWarsPlayerState.ACTION);
                        waitFor(800);
                        break ;
                    }
                    for (Unit target : area.getEnemies(this.getFaction())) {
                        DiscreteCoordinates position = new DiscreteCoordinates((int) target.getPosition().x, (int) target.getPosition().y);
                        if (!isInRange(position, selectedUnit)) continue;
                        targets.add(target);
                    }
                    actionToExecute = (targets.isEmpty()) ? new WaitAction(area, selectedUnit) : new AttackAction(area, selectedUnit);
                    setCurrentPlayerState(ICWarsPlayerState.ACTION);
                    waitFor(800);
                    break;
            case ACTION:
                    actionToExecute.doAutoAction(1.f, this);
                    break;
                default:
                    break;
        }
    }

    /**
     * Method returning the position where selectedUnit should move in order to get as close
     * as possible from the closest enemy unit, called in step MOVE_UNIT of the final state machine.
     */
    private DiscreteCoordinates getClosestPositionPossible() {
        DiscreteCoordinates closestUnitPosition = area.getClosestEnemyPosition(selectedUnit);
        boolean impossibleForAI = closestUnitPosition.x == 9 && (closestUnitPosition.y == 0 || closestUnitPosition.y == 1) && getOwnerArea().getTitle() == "icwars/Level1";
        int finalX = -1 ;
        int finalY = -1 ;
        int differenceX = closestUnitPosition.x - selectedUnitPosition.x ;
        int differenceY = closestUnitPosition.y - selectedUnitPosition.y ;

        //Handling the case when selectedUnit is already well positioned
        if(Math.abs(differenceY) <= 1 && Math.abs(differenceX) <= 1){
            return selectedUnitPosition;
        }

        //Handling the case when closestUnit is in the range of selectedUnit in  order to avoid the superposition
        if(isInRange(closestUnitPosition, selectedUnit)) {
            ArrayList<DiscreteCoordinates> coords = new ArrayList<DiscreteCoordinates>();

            int indexX = 1 ;
            while(finalX < 0) {
                if (differenceX < 0) { // closestUnit to the left of selectedUnit
                    try {
                        if(impossibleForAI) throw new WrongLocationException();
                        coords.add(new DiscreteCoordinates(closestUnitPosition.x + indexX, closestUnitPosition.y));
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalX = coords.get(0).x;
                    }
                }

                if (differenceX > 0) { // closestUnit to the right of selectedUnit
                    try {
                        if(impossibleForAI) throw new WrongLocationException();
                        coords.add(new DiscreteCoordinates(closestUnitPosition.x - indexX, closestUnitPosition.y));
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalX = coords.get(0).x;
                    }
                }

                if(differenceX == 0){ // closestUnit
                    try {
                        if(impossibleForAI) throw new WrongLocationException();
                        coords.add(closestUnitPosition);
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    finalX = coords.get(0).x;
                }
                ++indexX ;
                coords.clear();
            }

            int indexY = 1;
            while (finalY < 0) {
                if (differenceY < 0) { // closestUnit under selectedUnit
                    try {
                        if(impossibleForAI)throw new WrongLocationException();
                        coords.add(new DiscreteCoordinates(closestUnitPosition.x, closestUnitPosition.y + indexY));
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalY = coords.get(0).y;
                    }
                }

                if (differenceY > 0) { // closestUnit above selectedUnit
                    try {
                        if(impossibleForAI) throw new WrongLocationException();
                        coords.add(new DiscreteCoordinates(closestUnitPosition.x, closestUnitPosition.y - indexY));
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalY = coords.get(0).y;
                    }
                }

                if(differenceY == 0){
                    try {
                        if(impossibleForAI) throw new WrongLocationException();
                        coords.add(closestUnitPosition);
                    } catch(WrongLocationException e) {
                        return new DiscreteCoordinates(9, 2);
                    }
                    finalY = coords.get(0).y;
                }
                ++indexY;
                coords.clear();
            }

        } else {

            // Compare the x's
            //Check if closestUnitPosition.x is in the x-range of selectedUnit
            if (Math.abs(selectedUnitPosition.x - closestUnitPosition.x) <= selectedUnit.getRadius())
                finalX = closestUnitPosition.x;
            else finalX = optimalBorderXOrY(selectedUnitPosition.x, closestUnitPosition.x);


            // Compare the y's
            //Check if closestUnitPosition.y is in the y-range of selecteDunit
            if (Math.abs(selectedUnitPosition.y - closestUnitPosition.y) <= selectedUnit.getRadius())
                finalY = closestUnitPosition.y;
            else finalY = optimalBorderXOrY(selectedUnitPosition.y, closestUnitPosition.y);

        }

        DiscreteCoordinates closestPositionPossible = new DiscreteCoordinates(finalX, finalY);
        ArrayList<DiscreteCoordinates> coords = new ArrayList<DiscreteCoordinates>();
        coords.add(closestPositionPossible);

        //Handling the special case when target is not in range of targetUnit and optimal position is
        //already occupied by another unit
        int index = 1;
        while(!area.canEnterAreaCells(selectedUnit, coords)) {
            coords.clear();
            DiscreteCoordinates positionRight = new DiscreteCoordinates(closestPositionPossible.x + index, closestPositionPossible.y);
            DiscreteCoordinates positionLeft = new DiscreteCoordinates(closestPositionPossible.x - index, closestPositionPossible.y);
            DiscreteCoordinates positionDown = new DiscreteCoordinates(closestPositionPossible.x, closestPositionPossible.y + index);
            DiscreteCoordinates positionUp = new DiscreteCoordinates(closestPositionPossible.x + index, closestPositionPossible.y - index);

            if(isInRange(positionRight, selectedUnit)) {
                coords.clear();
                coords.add(positionRight);
            } else if(isInRange(positionLeft, selectedUnit)){
                coords.clear();
                coords.add(positionLeft);
            } else if(isInRange(positionDown, selectedUnit)){
                coords.clear();
                coords.add(positionDown);
            } else if (isInRange(positionUp, selectedUnit)){
                coords.clear();
                coords.add(positionUp);
            }
            ++index;
        }

        closestPositionPossible = coords.get(0);
        return closestPositionPossible ;
    }


    /**
     * Method returning the optimal range border (left or right if given x's and up or down if given y's)
     * to move to given the selectedUnitPosition.(x or y) and the closestUnitPositon.(x or y) :
     * we call it only after veryfing the closestUnit is not in the (x or y)-range.
     * @param selectedUnitPositionXOrY : the selectedUnit x or y
     * @param closestUnitPositionXOrY : the closestUnit x or y
     */
    private int optimalBorderXOrY(int selectedUnitPositionXOrY, int closestUnitPositionXOrY){
        assert(selectedUnitPositionXOrY - closestUnitPositionXOrY != 0 );
        int optimal ;
        double difference = selectedUnitPositionXOrY - closestUnitPositionXOrY ;
        if(difference < 0){
            optimal = selectedUnitPositionXOrY + selectedUnit.getRadius() ;
        } else {
            optimal = selectedUnitPositionXOrY - selectedUnit.getRadius() ;
        }
        return optimal ;
    }

    /**
     * Method checking if all of AIPlayer effectives have been used
     * @param effectives : List of AIPlayer effectives
     */
    private boolean haveBeenUsed(ArrayList<Unit> effectives) {
        for (Unit effective : effectives) {
            if (!effective.isHasBeenUsed()) return false;
        }
        return true;
    }


    /**
     * Method to call when needing to fill the general effectives list of AIPlayer and the effectives waiting
     * for current round (which have not been played yet).
     */
    private void fillEffectiveList(){
        counter = 0;
        aiEffectives.clear();
        for (int i = 0; i < getEffectivesSize(); i++) {
            aiEffectives.add(getUnitFromIndex(i));
        }
    }

    /**
     * Function making the program wait for a certain time before resuming action
     * @param ms : Time in milliseconds
     */
    private static void waitFor(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}


