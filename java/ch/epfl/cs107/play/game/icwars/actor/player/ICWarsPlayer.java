package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import java.util.ArrayList;

public abstract class ICWarsPlayer extends ICWarsActor {

    private ArrayList<Unit> effectives ;

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
        for (Unit unit : units) {
            effectives.add(unit);
            area.registerActor(unit);
        }
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
