package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.List;

public abstract class ICWarsPlayer extends ICWarsActor {

    private ArrayList<Unit> effectives ;
    private ICWarsPlayerState state ;
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
        state = ICWarsPlayerState.IDLE ;
        for (Unit unit : units) {
            effectives.add(unit);
            area.registerActor(unit);
        }
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
     * @param state
     */
    public void updateState(ICWarsPlayerState state){
        Keyboard keyboard = getOwnerArea().getKeyboard();
        switch (state){
            case IDLE : break ;
            case NORMAL :
                if(keyboard.get(Keyboard.ENTER).isReleased()) state = ICWarsPlayerState.SELECT_CELL ;
                break;
            case SELECT_CELL:
                if(selectedUnit != null) state = ICWarsPlayerState.MOVE_UNIT ;
                break;
            case MOVE_UNIT:
                //TODO move selected unit to current location
                if(keyboard.get(Keyboard.ENTER).isReleased()) state = ICWarsPlayerState.NORMAL ;
                break;
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
        state = ICWarsPlayerState.NORMAL;
        centerCamera();
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        if(state == ICWarsPlayerState.SELECT_CELL) state = ICWarsPlayerState.NORMAL ;
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        for (Unit effective : effectives) {
            area.registerActor(effective);
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

    /*
    /**
     * Method that select the unit corresponding to the index
     * passed in element in its array effectives. If the index is too
     * big, then the method just does nothing
     * @param index : integer representing the selected unit among the arraylist effectives
     *//*
    public void selectUnit(int index){
        if(index > effectives.size()) ;
        else {
            selectedUnit = effectives.get(index);
        }
    }
    */

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

    }
}
