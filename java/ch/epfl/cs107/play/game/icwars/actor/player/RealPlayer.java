package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.AttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ICWarsAction;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;


public class RealPlayer extends ICWarsPlayer {
    private final static int MOVE_DURATION = 4;
    private Sprite sprite ;
    private ICWarsPlayerGUI gui ;
    private final ICWarsPlayerInteractionHandler handler;
    private ICWarsBehavior.ICWarsCellType cellType;
    private Unit unitOnCell ;
    private ICWarsAction actionToExecute ;



    /**
     * Default constructor for ICWarsActor
     * @param area     : area in which the actor evolves
     * @param position : the coordinates of his exact position on the grid
     * @param faction  : player's faction : ALLY or ENEMY
     * @param units    : the units the player owns (as many as he wants because it is an ellipse)
     */
    public RealPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units) {
        super(area, position, faction, units);
        gui = new ICWarsPlayerGUI(area.getCameraScaleFactor(), this);
        handler = new ICWarsPlayerInteractionHandler();
        switch(faction) {
            case ALLY:
                sprite = new Sprite("icwars/allyCursor", 1.f, 1.f, this);
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(getCurrentPlayerState() != ICWarsPlayerState.IDLE) sprite.draw(canvas);
        if(selectedUnit != null && getCurrentPlayerState() == ICWarsPlayerState.MOVE_UNIT) gui.draw(canvas, selectedUnit);
        if(getCurrentPlayerState() == ICWarsPlayerState.NORMAL || getCurrentPlayerState() == ICWarsPlayerState.SELECT_CELL) gui.draw(canvas, unitOnCell, cellType);
        if(getCurrentPlayerState() == ICWarsPlayerState.ACTION_SELECTION) gui.draw(canvas, selectedUnit, true);
        if(getCurrentPlayerState() == ICWarsPlayerState.ACTION) actionToExecute.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {

        Keyboard keyboard= getOwnerArea().getKeyboard();
        if (getCurrentPlayerState() == ICWarsPlayerState.NORMAL || getCurrentPlayerState() == ICWarsPlayerState.SELECT_CELL || getCurrentPlayerState() == ICWarsPlayerState.MOVE_UNIT) {
            moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
            moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }
        controlUnits();
        updatePlayerState();
        super.update(deltaTime);
    }

    /**
     * Method to update player's state if certain conditions are met
     * The method is called by the update method of RealPlayer.java
     */
    private void updatePlayerState(){
        Keyboard keyboard = getOwnerArea().getKeyboard();
        switch (getCurrentPlayerState()) {
            case NORMAL:
                centerCamera();
                if (keyboard.get(Keyboard.TAB).isPressed()){
                    setCurrentPlayerState(ICWarsPlayerState.IDLE);
                }
                if (keyboard.get(Keyboard.ENTER).isReleased()) setCurrentPlayerState(ICWarsPlayerState.SELECT_CELL);
                break;
            case SELECT_CELL:
                if (selectedUnit != null && !selectedUnit.isHasBeenUsed()) setCurrentPlayerState(ICWarsPlayerState.MOVE_UNIT);
                break;
            case MOVE_UNIT:
                if (keyboard.get(Keyboard.TAB).isReleased()){
                    setCurrentPlayerState(ICWarsPlayerState.NORMAL);
                }
                if(keyboard.get(Keyboard.ENTER).isReleased()){
                    if(getOwnerArea().canEnterAreaCells(selectedUnit, getCurrentCells())|| getCoordinates().toVector().equals(selectedUnit.getPosition())) {
                        selectedUnit.changePosition(getCoordinates());
                        setCurrentPlayerState(ICWarsPlayerState.ACTION_SELECTION);
                    }
                }
                break;
            case ACTION_SELECTION:
                actionToExecute = new AttackAction((ICWarsArea)getOwnerArea(), selectedUnit);
                for (ICWarsAction act : selectedUnit.actionsList) {
                    if(keyboard.get(act.getKey()).isPressed()){
                        actionToExecute = act ;
                        //Resetting index in AttackAction only once before calling doAction()
                        //in order to take in consideration the update of the unitList of the
                        //ICWarsArea
                        if(actionToExecute instanceof AttackAction){
                            AttackAction attackActionToExecute = (AttackAction) actionToExecute ;
                            attackActionToExecute.setIndex(0);
                        }
                        setCurrentPlayerState(ICWarsPlayerState.ACTION);
                    }
                }
                break;
            case ACTION:
                actionToExecute.doAction(1.f, this, keyboard);
                break;
            case IDLE:
            default: break;
        }
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        unitOnCell = null;
    }

    /**
     * Orientate and Move this player in the given orientation if the given button is down
     * @param orientation (Orientation): given orientation, not null
     * @param b (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b){
        if(b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    public void interactWith(Interactable other) {
        if(!isDisplacementOccurs()) {
            other.acceptInteraction(handler);
        }
    }

    private class ICWarsPlayerInteractionHandler implements ICWarInteractionVisitor {

        @Override
        public void interactWith(Unit unit) {
            if (getCurrentPlayerState() == ICWarsPlayerState.SELECT_CELL && unit.getFaction() == getFaction()) {
                selectedUnit = unit;
            }
            unitOnCell = unit;
        }

        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell cell) {
            cellType = cell.getType();
        }
    }
}
