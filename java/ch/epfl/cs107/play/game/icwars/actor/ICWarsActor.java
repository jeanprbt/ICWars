package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.awt.image.DirectColorModel;
import java.util.Collections;
import java.util.List;

public abstract class ICWarsActor extends MovableAreaEntity {

    private Faction faction;

    //-----------------------------------API-------------------------------------//

    /**
     * Default constructor for ICWarsActor
     *
     * @param area     : area in which the actor evolves
     * @param position : the coordinates of his exact position on the grid
     */
    public ICWarsActor(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, Orientation.UP, position);
        this.faction = faction;
    }

    /**
     * Enumeration for the type of an ICWarsActor : Ally or Ennemy
     */
    public enum Faction {ALLY, ENEMY}

    //Getter for faction
    public Faction getFaction() {
        return faction;
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Enter a new area and register player
     * @param area     (Area): initial area, not null
     * @param position (DiscreteCoordinates): initial position, not null
     */
    public void enterArea(ICWarsArea area, DiscreteCoordinates position) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
    }

    /**
     * Leave an area and unregister player
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }
}
