package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.Vector;

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
        switch (faction) {
            case ALLY:
                sprite = new Sprite("icwars/friendlyTank", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
            case ENEMY:
                sprite = new Sprite("icwars/enemyTank", 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
                break;
        }
    }


    public DiscreteCoordinates getSpawnCoordinates(){
        return new DiscreteCoordinates(3, 5);
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


