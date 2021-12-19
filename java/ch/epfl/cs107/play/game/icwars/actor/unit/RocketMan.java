package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.AttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ICWarsAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.RocketManAttackAction;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.WaitAction;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;

public class RocketMan extends Unit {

    //-----------------------------------API-------------------------------------//

    /**
     * Default constructor for a soldier. Depending on its faction it
     * gives it the correct sprite.
     * @param area : the area in which evolves the soldier
     * @param position : the coordinates of the position of the soldier on the grid
     * @param faction : the faction of the soldier : ALLY or ENEMY
     */
    public RocketMan(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, position, faction);
        hp = getMaxHp() ;
        unitType = UnitType.ROCKETMAN;
        actionsList = new ArrayList<ICWarsAction>();
        actionsList.add(new RocketManAttackAction((ICWarsArea)area, this));
        actionsList.add(new WaitAction((ICWarsArea) area, this));
        switch (faction){
            case ALLY:
                sprite = new Sprite("icwars/friendlyRocket", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyRocket", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
        }
    }

    public static DiscreteCoordinates getSpawnCoordinates(Faction faction){
        DiscreteCoordinates coordinates ;
        coordinates = (faction == Faction.ALLY) ? new DiscreteCoordinates(2, 4) : new DiscreteCoordinates(8, 4);
        return coordinates;
    }
    
    public static int getDamageZone(){
        return 3 ;
    }

    @Override
    public int getDamage() {
        return 3;
    }
    @Override
    public int getRadius() {
        return 2;
    }
    @Override
    public int getMaxHp() {
        return 4;
    }
    @Override
    public String getName() {
        return "RocketMan";
    }


}
