package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.AttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ICWarsAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.WaitAction;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import java.util.ArrayList;

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
                    changePosition(new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int) selectedUnit.getPosition().y));
                    waitFor(300);
                    setCurrentPlayerState(ICWarsPlayerState.MOVE_UNIT);
                }
                ++counter;
                break ;
                case MOVE_UNIT:
                    waitFor(300);
                    changePosition(getClosestPositionPossible());
                    selectedUnit.setHasBeenUsed(true);
                    waitFor(300);
                    selectedUnit.changePosition(getClosestPositionPossible());
                    setCurrentPlayerState(ICWarsPlayerState.NORMAL);
                    break;
                case ACTION_SELECTION:
                    selectedUnitPosition = new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int)selectedUnit.getPosition().y);
                    ArrayList<Unit> targets = new ArrayList<Unit>();
                    for (Unit target: area.getEnemies(this.getFaction())) {
                        DiscreteCoordinates position = new DiscreteCoordinates((int)target.getPosition().x, (int)target.getPosition().y);
                        if(!isInRange(position)) continue ;
                        targets.add(target);
                    }
                    actionToExecute = (targets.isEmpty())? new WaitAction(area, selectedUnit) : new AttackAction(area, selectedUnit);
                    setCurrentPlayerState(ICWarsPlayerState.ACTION);
                case ACTION:
                    actionToExecute = new AttackAction((ICWarsArea)getOwnerArea(), selectedUnit);
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
    private DiscreteCoordinates getClosestPositionPossible(){
        DiscreteCoordinates closestUnitPosition = area.getClosestEnemyPosition(selectedUnit);
        int finalX ;
        int finalY ;

        //Handling the case when closestUnit is in the range of selectedUnit in  order to avoid the superposition
        if(isInRange(closestUnitPosition)){
            int differenceX = closestUnitPosition.x - selectedUnitPosition.x ;
            int differenceY = closestUnitPosition.y - selectedUnitPosition.y ;
            ArrayList<DiscreteCoordinates> coords = new ArrayList<DiscreteCoordinates>();

            while(finalX < 0) {
                int index = 1 ;
                if (differenceX < 0) { // closestUnit to the left of selectedUnit
                    coords.add(new DiscreteCoordinates(closestUnitPosition.x + index, closestUnitPosition.y));
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalX = coords.get(0).x;
                    }
                }

                if (differenceX > 0) { // closestUnit to the right of selectedUnit
                    coords.add(new DiscreteCoordinates(closestUnitPosition.x - index, closestUnitPosition.y));
                    if (area.canEnterAreaCells(selectedUnit, coords)) {
                        finalX = coords.get(0).x;
                    }
                }

                if(differenceX == 0){ // closestUnit
                    coords.add(closestUnitPosition);
                    finalX = coords.get(0).x;
                }
                ++index ;
                coords.clear();
            }

           while (finalY < 0) {
               int index = 1;
               if (differenceY < 0) { // closestUnit under selectedUnit
                   coords.add(new DiscreteCoordinates(closestUnitPosition.x, closestUnitPosition.y + index));
                   if (area.canEnterAreaCells(selectedUnit, coords)) {
                       finalY = coords.get(0).y;
                   }
               }

               if (differenceY > 0) { // closestUnit above selectedUnit
                   coords.add(new DiscreteCoordinates(closestUnitPosition.x, closestUnitPosition.y - index));
                   if (area.canEnterAreaCells(selectedUnit, coords)) {
                       finalY = coords.get(0).y;
                   }
               }

               if(differenceY == 0){
                   coords.add(closestUnitPosition);
                   finalY = coords.get(0).y;
               }
               ++index;
               coords.clear();
           }
           return new DiscreteCoordinates(finalX, finalY);
        }

        // Compare the x's
        //Check if closestUnitPosition.x is in the x-range of selectedUnit
        if (Math.abs(selectedUnitPosition.x - closestUnitPosition.x) <= selectedUnit.getRadius()) finalX = closestUnitPosition.x;
        else finalX = optimalBorderXOrY(selectedUnitPosition.x, closestUnitPosition.x);

        // Compare the y's
        //Check if closestUnitPosition.y is in the y-range of selecteDunit
        if(Math.abs(selectedUnitPosition.y - closestUnitPosition.y) <= selectedUnit.getRadius()) finalY = closestUnitPosition.y;
        else finalY = optimalBorderXOrY(selectedUnitPosition.y, closestUnitPosition.y);

        DiscreteCoordinates closestPositionPossible = new DiscreteCoordinates(finalX, finalY);
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
     * Method testing if a given position is in the range of selectedUnit
     * @param position the position to test
     */
    private boolean isInRange(DiscreteCoordinates position){
        boolean isInRangeX = position.x <= selectedUnitPosition.x + selectedUnit.getRadius() && position.x >= selectedUnitPosition.x - selectedUnit.getRadius();
        boolean isInRangeY = position.y <= selectedUnitPosition.y + selectedUnit.getRadius() && position.y >= selectedUnitPosition.y - selectedUnit.getRadius();
        return isInRangeX && isInRangeY ;
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


