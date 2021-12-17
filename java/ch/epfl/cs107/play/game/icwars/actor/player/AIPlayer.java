package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.AttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ICWarsAction;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.exception.WrongLocationException ;
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
                fillEffectiveList();
                break;
            case NORMAL:
                centerCamera();
                if(hasBeenUsed(aiEffectives)) {
                    setCurrentPlayerState(ICWarsPlayerState.IDLE);
                }
                else {
                    selectedUnit = aiEffectives.get(counter);
                    selectedUnitPosition = new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int)selectedUnit.getPosition().y);
                    waitFor(500);
                    changePosition(new DiscreteCoordinates((int) selectedUnit.getPosition().x, (int) selectedUnit.getPosition().y));
                    waitFor(500);
                    setCurrentPlayerState(ICWarsPlayerState.MOVE_UNIT);
                }
                ++counter;
                break ;
                case MOVE_UNIT:
                    selectedUnit.setHasBeenUsed(true);
                    waitFor(800);
                    changePosition(getClosestPositionPossible());
                    waitFor(800);
                    selectedUnit.changePosition(getClosestPositionPossible());
                    setCurrentPlayerState(ICWarsPlayerState.NORMAL);
                    break;
                case ACTION:
                    actionToExecute = new AttackAction((ICWarsArea)getOwnerArea(), selectedUnit);
                    actionToExecute.doAutoAction(1.f, this);
                    break;
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
     * Method returning the position where selectedUnit should move in order to get as close
     * as possible from the closest enemy unit, called in step MOVE_UNIT of the final state machine.
     */
    private DiscreteCoordinates getClosestPositionPossible(){
        ICWarsArea area = (ICWarsArea) getOwnerArea() ;
        DiscreteCoordinates closestUnitPosition = area.getClosestEnemyPosition(selectedUnit);
        int finalX ;
        int finalY ;

        //Handling the case when closestUnit is in the range of selectedUnit in  order to avoid the superposition
        if(isInRange(closestUnitPosition)){
           while(!(isInRange(closestUnitPosition))) {
                try {
                    changePosition(closestUnitPosition);
                } catch (Exception e){

                }
            }
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
    private boolean hasBeenUsed(ArrayList<Unit> effectives) {
        for (Unit effective : effectives) {
            if (!effective.isHasBeenUsed()) return false;
        }
        return true;
    }



    @Override
    public void draw(Canvas canvas) {
        if(getCurrentPlayerState() != ICWarsPlayerState.IDLE) sprite.draw(canvas);
    }
}


