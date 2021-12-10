package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.AttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ICWarsAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.WaitAction;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;

import java.util.ArrayList;
import java.util.List;

public class Tank extends Unit {

    /**
     * Default constructor for a tank. Depending on its faction it
     * gives it the correct sprite.
     * @param area : the area in which evolves the tank
     * @param position : the coordinates of the position of the tank on the grid
     * @param faction : the faction of the tank : ALLY or ENEMY
     */
    public Tank(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, position, faction);
        hp = getMaxHp();
        unitType = UnitType.TANK;
        actionsList.add(new AttackAction((ICWarsArea)area, this));
        actionsList.add(new WaitAction((ICWarsArea)area, this));
        switch (faction) {
            case ALLY:
                sprite = new Sprite("icwars/friendlyTank", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyTank", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
        }
    }


    public static DiscreteCoordinates getSpawnCoordinates(Faction faction){
        DiscreteCoordinates coordinates ;
        coordinates = (faction == Faction.ALLY) ? new DiscreteCoordinates(2, 5) : new DiscreteCoordinates(8, 5);
        return coordinates;
    }


    public int getDamage() {
        return 7;
    }
    public int getRadius(){
        return 4 ;
    }
    public int getMaxHp(){
        return 10;
    }
    public String getName(){
        return "Tank";
    }
}

