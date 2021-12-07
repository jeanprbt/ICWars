package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.game.areagame.actor.Sprite ;

public class Soldier extends Unit {

    /**
     * Default constructor for a soldier. Depending on its faction it
     * gives it the correct sprite.
     * @param area : the area in which evolves the soldier
     * @param position : the coordinates of the position of the soldier on the grid
     * @param faction : the faction of the soldier : ALLY or ENEMY
     */
    public Soldier(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, position, faction);
        hp = getMaxHp() ;
        switch (faction){
            case ALLY:
                sprite = new Sprite("icwars/friendlySoldier", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemySoldier", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
        }
    }

    public DiscreteCoordinates getSpawnCoordinates(){
        return new DiscreteCoordinates(2, 5);
    }

    public int getDamage(){
        return 2 ;
    }
    public int getRadius(){
        return 2 ;
    }
    public int getMaxHp(){
        return 5 ;
    }
    public String getName(){
        return "Soldier";
    }
}
