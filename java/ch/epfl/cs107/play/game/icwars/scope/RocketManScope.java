package ch.epfl.cs107.play.game.icwars.scope;

import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.city.ICWarsCity;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class RocketManScope extends ICWarsActor implements Interactor {

    private final static int MOVE_DURATION = 4;
    private Sprite sprite ;
    private RocketManScopeInteractionHandler handler;
    private ArrayList<Unit> targets ;
    private int range ;
    private Animation animation ;
    private Sprite[] sprites ;
    private boolean isInProgress ;
    private boolean isReal ;
    private boolean aiHasMovedScope ;
    private boolean hasCollectedTargets ;

    //-----------------------------------API-------------------------------------//

    public RocketManScope(ICWarsArea area, DiscreteCoordinates position, Faction faction, int range, boolean isReal){
        super(area, position, faction);
        this.isReal = isReal ;
        aiHasMovedScope = false ;
        hasCollectedTargets = false ;
        sprites = new Sprite[7];
        sprites[0] = new Sprite("1", 3, 3);
        sprites[1] = new Sprite("2", 3, 3);
        sprites[2] = new Sprite("3", 3, 3);
        sprites[3] = new Sprite("4", 3, 3);
        sprites[4] = new Sprite("5", 3, 3);
        sprites[5] = new Sprite("6", 3, 3);
        sprites[6] = new Sprite("7", 3, 3);
        animation = new Animation(3, sprites, false);
        switch (faction){
            case ALLY:
                sprite = new Sprite("icwars/allyCursor", 1.f, 1.f, this);
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
                break;
        }
        sprite.setHeight(range);
        sprite.setWidth(range);
        targets = new ArrayList<Unit>();
        this.range = range ;
        handler = new RocketManScopeInteractionHandler();
    }

    //Getter for targets
    public ArrayList<Unit> getTargets() {
        return targets;
    }

    //Getter used in RocketManAttackAction
    public boolean hasCollectedTargets() {
        return hasCollectedTargets;
    }

    //Setter used in RocketManAttackAction
    public void setAiHasMovedScope(boolean aiHasMovedScope) {
        this.aiHasMovedScope = aiHasMovedScope;
    }

    //Getter used for animations
    public boolean isInProgress(){
        return isInProgress;
    }


    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        if (isInProgress){
            animation.update(1);
            animation.draw(canvas);
        }
    }

    public boolean animationCompleted(){
        return animation.isCompleted() ;
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
        targets.clear();
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
    public void acceptInteraction(AreaInteractionVisitor v) {}

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

    public void resetPosition(Vector position){
        changePosition(new DiscreteCoordinates((int)position.x, (int)position.y));
    }

    //-----------------------------------Private-------------------------------------//

    private void moveIfPressed(Orientation orientation, Button b){
        if(isReal) {
            if (!isDisplacementOccurs() && b.isDown()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    private class RocketManScopeInteractionHandler implements ICWarInteractionVisitor {

        //-----------------------------------API-------------------------------------//

        @Override
        public void interactWith(Unit unit) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            if(isReal) {
                if (keyboard.get(Keyboard.ENTER).isReleased()) {
                    for (int i = 0; i < sprites.length; i++) {
                        sprites[i].setAnchor(getPosition());
                    }
                    hasCollectedTargets = true;
                    isInProgress = true;
                    targets.add(unit);
                }
            }

            if (!isReal && aiHasMovedScope){
                for (int i = 0; i < sprites.length; i++) {
                    sprites[i].setAnchor(getPosition());
                }
                isInProgress = true ;
                targets.add(unit);
                hasCollectedTargets = true ;

            }
            if(animation.isCompleted()) {
                isInProgress = false ;
            }
        }

        @Override
        public void interactWith(ICWarsCity city) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            //if a city is in sight of a RocketMan's attack it gets destroyed and goes back to neutral
            if (keyboard.get(Keyboard.ENTER).isReleased() && !targets.isEmpty() && isReal) {
                city.isCaptured(Faction.NEUTRAL);
            }
            if (!isReal && aiHasMovedScope)city.isCaptured(Faction.NEUTRAL);
        }
    }
}
