package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;


public class RealPlayer extends ICWarsPlayer {
    private final static int MOVE_DURATION = 4;
    private Sprite sprite ;
    private ICWarsPlayerGUI gui ;
    private final ICWarsPlayerInteractionHandler handler;


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
        if(selectedUnit != null && currentPlayerState == ICWarsPlayerState.MOVE_UNIT) gui.draw(canvas, selectedUnit);
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard= getOwnerArea().getKeyboard();
        if (currentPlayerState == ICWarsPlayerState.NORMAL || currentPlayerState == ICWarsPlayerState.SELECT_CELL || currentPlayerState == ICWarsPlayerState.MOVE_UNIT) {
            moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
            moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }
        updateState();
        super.update(deltaTime);
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
        other.acceptInteraction(handler);
    }

    private class ICWarsPlayerInteractionHandler implements ICWarInteractionVisitor {

        @Override
        public void interactWith(Unit unit) {
            if (currentPlayerState == ICWarsPlayerState.SELECT_CELL && unit.getFaction() == getFaction()) {
                if (selectedUnit != null) selectedUnit.setSelectedUnit(false);
                selectedUnit = unit;
                unit.setSelectedUnit(true);
            }
        }
    }
}
