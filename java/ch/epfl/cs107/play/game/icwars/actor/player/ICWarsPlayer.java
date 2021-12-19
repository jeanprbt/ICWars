package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class ICWarsPlayer extends ICWarsActor implements Interactor {
    protected Unit selectedUnit ;

    private ICWarsPlayerState currentPlayerState ;
    private ArrayList<Unit> effectives ;


    //-----------------------------------API-------------------------------------//

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
        currentPlayerState = ICWarsPlayerState.IDLE ;
        for (Unit unit : units) {
            effectives.add(unit);
            area.registerActor(unit);
        }
    }

    /**
     * Enumeration of all possible states of a player
     * during the game :IDLE, NORMAL, SELECT_CELL, MOVE_UNIT,
     * ACTION_SELECTION, ACTION
     */
    public enum ICWarsPlayerState {
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTION,
        ACTION
    }

    //Getter and setter for currentPlayerState
    public ICWarsPlayerState getCurrentPlayerState() {
        return currentPlayerState;
    }
    public void setCurrentPlayerState(ICWarsPlayerState state){
        this.currentPlayerState = state ;
    }

    //Getters for effectives : getter from index and size getter
    public Unit getUnitFromIndex(int index){
        return effectives.get(index);
    }
    public int getEffectivesSize(){
        return effectives.size();
    }

    @Override
    public void enterArea(ICWarsArea area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        for (Unit effective : effectives) {
            effective.enterArea(area, effective.getSpawnCoordinates(effective.getFaction(), effective.unitType));
            effective.fillRange(effective.getSpawnCoordinates(effective.getFaction(), effective.unitType));
            area.addToUnitList(effective);
        }
    }

    @Override
    public void leaveArea() {
        super.leaveArea();
        for (Unit effective : effectives) {
            getOwnerArea().unregisterActor(effective);
        }
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        if(currentPlayerState == ICWarsPlayerState.SELECT_CELL) currentPlayerState = ICWarsPlayerState.NORMAL;
    }

    @Override
    public void update(float deltaTime) {
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

    /**
     * Method that sets all units as available for asked player
     * (used in updateState in ICWars.java)
     */
    public void setUnitsAvailable() {
        for (Unit effective : effectives) {
            effective.setHasBeenUsed(false);
        }
    }

    /**
     * Method to call when a new player is starting his turn
     * The method changes its state to NORMAL and centers the camera
     * on itself
     */
    public void startTurn() {
        currentPlayerState = ICWarsPlayerState.NORMAL;
        centerCamera();
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

    //-----------------------------------Protected-------------------------------------//

    /**
     * Method to call at then end of the selectedUnit's action to check if units died
     */
    protected void controlUnits() {
        ArrayList<Unit> effectivesToRemove = new ArrayList<Unit>();
        for (Unit effective : effectives) {
            if(effective.isDead()) {
                ICWarsArea area = (ICWarsArea) getOwnerArea() ;
                area.removeFromUnitList(effective);
                getOwnerArea().unregisterActor(effective);
                effectivesToRemove.add(effective);
            }
        }
        effectives.removeAll(effectivesToRemove);
    }

    /**
     * Method testing if a given position is in the range of selectedUnit
     * @param position the position to test
     */
    protected boolean isInRange(DiscreteCoordinates position, Unit selectedUnit){
        boolean isInRangeX = position.x <= selectedUnit.getPosition().x + selectedUnit.getRadius() && position.x >= selectedUnit.getPosition().x - selectedUnit.getRadius();
        boolean isInRangeY = position.y <= selectedUnit.getPosition().y + selectedUnit.getRadius() && position.y >= selectedUnit.getPosition().y - selectedUnit.getRadius();
        return isInRangeX && isInRangeY ;
    }
}
