package ch.epfl.cs107.play.game.icwars.actor.city;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsCity extends ICWarsActor {
    private Sprite sprite ;

    //-----------------------------------API-------------------------------------//

    public ICWarsCity(Area area, DiscreteCoordinates position) {
        super(area, position, Faction.NEUTRAL);
        sprite = new Sprite("icwars/neutralBuilding", 1.f, 1.f, this);
    }

    public void isCaptured(Faction faction){
        setFaction(faction);
        switch (faction){
            case ALLY:
                sprite = new Sprite("icwars/friendlyBuilding", 1.f, 1.f, this);
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyBuilding", 1.f, 1.f, this);
                break;
            case NEUTRAL:
                sprite = new Sprite("icwars/neutralBuilding", 1.f, 1.f, this);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
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
    public void acceptInteraction(AreaInteractionVisitor v)  {
        ((ICWarInteractionVisitor) v).interactWith(this);
    }
}
