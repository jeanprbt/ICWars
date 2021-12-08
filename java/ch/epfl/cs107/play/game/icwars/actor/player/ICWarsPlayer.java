package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

public abstract class ICWarsPlayer extends ICWarsActor implements Interactor {

    private ArrayList<Unit> effectives ;
    protected ICWarsPlayerState currentState ;
    protected Unit selectedUnit ;

    /**
     * Default constructor for ICWarsActor
     * @param area     : area in which the actor evolves
     * @param position : the coordinates of his exact position on the grid
     * @param faction : player's faction : ALLY or ENEMY
     * @param units : the units the player owns (as many as he wants because it is an ellipse)
     */
    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units) {
        super(area, position, faction);
        effectives = new ArrayList<Unit>();
        currentState = ICWarsPlayerState.IDLE ;
        for (Unit unit : units) {
            effectives.add(unit);
            area.registerActor(unit);
        }
    }

    /**
     * Method that returns the player's current position in the form of DiscreteCoordinates
     * @return current Discrete Coordinates
     */
    public DiscreteCoordinates getCoordinates() {
        return new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y);
    }

    /**
     * Enumeration of all possible states of a player
     * during the game :IDLE, NORMAL, SELECT_CELL, MOVE_UNIT,
     * ACTION_SELECTION, ACTION
     *
     */
    public enum ICWarsPlayerState {
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTION,
        ACTION
    }

    /**
     * Method to update player's state if certain conditions are met
     * The method is called by the update method of RealPlayer.java
     */
    public void updateState(){
        Keyboard keyboard = getOwnerArea().getKeyboard();
        switch (currentState) {
            case NORMAL:
                if (keyboard.get(Keyboard.TAB).isPressed()) currentState = ICWarsPlayerState.IDLE;
                if (keyboard.get(Keyboard.ENTER).isReleased()) currentState = ICWarsPlayerState.SELECT_CELL;
                break;
            case SELECT_CELL:
                if (selectedUnit != null && !selectedUnit.isHasBeenUsed()) currentState = ICWarsPlayerState.MOVE_UNIT;
                break;
            case MOVE_UNIT:
                if (keyboard.get(Keyboard.TAB).isReleased()){
                    currentState = ICWarsPlayerState.NORMAL;
                }
                if(keyboard.get(Keyboard.ENTER).isReleased()){
                    selectedUnit.changePosition(getCoordinates());
                    currentState = ICWarsPlayerState.NORMAL ;
                }
                break;
            case IDLE:
            case ACTION:
            case ACTION_SELECTION:
            default: break;
        }
    }

    /**
     * Method to call when a new player is starting his turn
     * The method changes its state to NORMAL and centers the camera
     * on itself
     */
    public void startTurn() {
        currentState = ICWarsPlayerState.NORMAL;
        centerCamera();
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        if(currentState == ICWarsPlayerState.SELECT_CELL) currentState = ICWarsPlayerState.NORMAL;
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        for (Unit effective : effectives) {
            area.registerActor(effective);
            effective.setCurrentPosition(effective.getSpawnCoordinates(effective.getFaction(), effective.unitType).toVector());
            effective.fillRange(effective.getSpawnCoordinates(effective.getFaction(), effective.unitType));
            effective.setHasBeenUsed(false);
            effective.setOwnerArea(area);
        }
    }

    @Override
    public void leaveArea() {
        super.leaveArea();
        for (Unit effective : effectives) {
            getOwnerArea().unregisterActor(effective);
        }
    }

    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    /**
     * Returns if the player is defeated or not
     * based on if all its effectives are destroyed
     */
    public boolean isVanquished(){
        return(effectives.size() == 0);
    }

    @Override
    public void update(float deltaTime) {
        for (Unit effective : effectives) {
            if(effective.isDead()) {
                effectives.remove(effective);
                getOwnerArea().unregisterActor(effective);
            }
        }
        super.update(deltaTime);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarInteractionVisitor)v).interactWith(this);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {

    }
}
