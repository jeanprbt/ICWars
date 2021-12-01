package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Path;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Queue;

public abstract class Unit extends ICWarsActor {
    private String name ;
    private int radius ;
    private int maxHp ;
    private ICWarsRange range ;

    protected int hp ;
    protected Sprite sprite ;

    private boolean isDead = false ;

    abstract int getDamage();
    abstract int getRadius();
    abstract int getMaxHp();
    abstract String getName();



    /**
     * Default constructor for an Unit
     * It adds all possible nodes within the max range around
     * the unit to create a path
     * @param area : the area in which evolves the unit
     * @param position : the coordinates of its position in the area
     * @param faction : its faction : ALLY or ENEMY
     */
    public Unit(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, position, faction);

        for (int x = -radius ; x <= radius ; x++) {
            for (int y = -radius; y <= radius; y++) {

            }
        }
    }

    public int getHp() {
        return hp;
    }
    public boolean isDead(){
        return isDead;
    }

    /**
     * Function that lowers the health points and determines if
     * the unit is dead or not, it also returns 0 if health points
     * are negative
     * @param damage : the number of healthPoints to decrease
     */
    public void takeInjure(int damage){
        hp  -= damage ;
        hp = (hp <= 0) ? 0 : hp ;
        if (hp == 0) isDead = true ;
    }

    /**
     * Function that lowers the health points
     * The unit's hp after cannot exceed its maxHP
     * @param  : the number of healthPoints to increase
     */
    public void takeRepair(int repair){
        hp += repair ;
        hp = (hp >= getMaxHp()) ? getMaxHp() : hp ;
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

