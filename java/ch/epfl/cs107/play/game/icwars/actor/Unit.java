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
    private boolean hasBeenUsed ;
    private boolean isDead ;

    protected int hp ;
    protected Sprite sprite ;

    public UnitType unitType ;

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
        range = new ICWarsRange();
        radius = getRadius() ;
        name = getName() ;
        maxHp = getMaxHp() ;
        hasBeenUsed = false ;
        isDead = false ;
        fillRange(position);
    }

    /**
     * Enumeration of all possible types of unit
     */
    public enum UnitType {
        SOLDIER,
        TANK
    }

    public int getHp() {
        return hp;
    }
    public boolean isDead(){
        return isDead;
    }

    //Getter and Setter for hasBeenUsed
    public boolean isHasBeenUsed() {
        return hasBeenUsed;
    }
    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public void setCurrentPosition(Vector v) {
        super.setCurrentPosition(v);
    }

    /**
     * Function that returns the spawn coordinates of the desired unit, according to
     * its faction and type (soldier, tank, etc.)
     * It calls the static function getSpawnCoordinates() of the type in argument
     * @param faction : Ally or Enemy
     * @param unitType : Soldier or Tank
     */
    public DiscreteCoordinates getSpawnCoordinates(Faction faction, UnitType unitType){
        switch (unitType){
            case TANK:
                return Tank.getSpawnCoordinates(faction);
            case SOLDIER:
                return Soldier.getSpawnCoordinates(faction);
        }
        return null ;
    }

    /**
     * Method that modifies the attribute range :
     * Il creates a new empty ICWarsRange, and then fills it with all the nodes that
     * fits its conditions.
     * @param  : pair of DiscreteCoordinates from which the range is being created
     * @return : the new full ICWarsRange
     */
    public ICWarsRange fillRange(DiscreteCoordinates position){
        range = new ICWarsRange();
        for (int x = -radius ; x <= radius ; x++) {
            for (int y = -radius; y <= radius; y++) {
                boolean hasLeftEdge, hasRightEdge, hasDownEdge, hasUpEdge ;
                hasLeftEdge = x > (-radius) && (x + position.x) > 0;
                hasRightEdge = x < radius && (x + position.x) < (radius + position.x);
                hasUpEdge = y > (-radius) && (y + position.y) > 0;
                hasDownEdge = y < radius && (y + position.y) < (radius + position.y);
                range.addNode(new DiscreteCoordinates(position.x + x,position.y + y), hasLeftEdge, hasUpEdge, hasRightEdge, hasDownEdge);
            }
        }
        return range ; 
    }

    @Override
    public boolean changePosition(DiscreteCoordinates newPosition) {
        if (!range.nodeExists(newPosition)) {
            return false;
        } else if (super.changePosition(newPosition)) {
            hasBeenUsed = true;
            fillRange(newPosition);
            return true;
        }
        return false;
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

    /**
     * Draw the unit's range and a path from the unit position to
     destination
     * @param destination path destination
     * @param canvas canvas
     */
    public void drawRangeAndPathTo(DiscreteCoordinates destination, Canvas canvas) {
        range.draw(canvas);
        Queue<Orientation> path = range.shortestPath(getCurrentMainCellCoordinates(), destination);
        //Draw path only if it exists (destination inside the range)
        if (path != null){
            new Path(getCurrentMainCellCoordinates().toVector(), path).draw(canvas);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        if (isHasBeenUsed()) sprite.setAlpha(0.3f);
        else sprite.setAlpha(1.f);
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
            v.interactWith(this);
    }
}

