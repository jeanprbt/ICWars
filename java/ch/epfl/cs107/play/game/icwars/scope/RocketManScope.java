package ch.epfl.cs107.play.game.icwars.scope;

import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.List;

//-----------------------------------API-------------------------------------//

public class RocketManScope extends ICWarsActor implements Interactor {
    private final static int MOVE_DURATION = 4;
    private Sprite sprite ;
    private RocketManScopeInteractionHandler handler;
    private ArrayList<Unit> targets ;
    private int range ;
    private boolean hasCollectedTargets = false ;


    public RocketManScope(ICWarsArea area, DiscreteCoordinates position, Faction faction, int range){
        super(area, position, faction);
        sprite = new Sprite("icwars/allyCursor", 1.f, 1.f, this);
        sprite.setHeight(range);
        sprite.setWidth(range);
        targets = new ArrayList<Unit>();
        this.range = range ;
        handler = new RocketManScopeInteractionHandler();
    }

    public ArrayList<Unit> getTargets() {
        return targets;
    }

    public boolean hasCollectedTargets() {
        return hasCollectedTargets;
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    private void moveIfPressed(Orientation orientation, Button b){
        if(!isDisplacementOccurs()) {
            if (b.isDown()) {{
                orientate(orientation);
                move(MOVE_DURATION);
                }
            }
        }
    }

    public void centerCamera(){
        getOwnerArea().setViewCandidate(this);
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard= getOwnerArea().getKeyboard();
        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        super.update(deltaTime);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        ArrayList<DiscreteCoordinates> currentCells = new ArrayList<DiscreteCoordinates>();
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range ; j++) {
                currentCells.add(new DiscreteCoordinates((int) getPosition().x + i, (int) getPosition().y + j));
            }
        }
        return currentCells ;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true ;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
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
        other.acceptInteraction(handler);
    }
    private class RocketManScopeInteractionHandler implements ICWarInteractionVisitor {

        //-----------------------------------API-------------------------------------//

        @Override
        public void interactWith(Unit unit) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            if (keyboard.get(Keyboard.ENTER).isReleased()) {
                targets.add(unit);
                hasCollectedTargets = true;
            }
        }
    }
}
