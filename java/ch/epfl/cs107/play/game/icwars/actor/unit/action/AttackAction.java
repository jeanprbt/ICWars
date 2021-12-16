package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.util.ArrayList;

public class AttackAction extends ICWarsAction{

    private ImageGraphics cursor;
    private int targetIndex ;
    private ArrayList<Integer> targetsIndexes;
    private int index;
    // Boolean used to make sure that method draw of AttackAction is called after doAction
    // Without it method draw is called before and error occurs because targetIndexes is not
    // updated soon enough
    private boolean waitingPurposeBoolean;

    public AttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit, Keyboard.A, "(A)ttack");
        index = 0 ;
        cursor = new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"), 1f, 1f, new RegionOfInterest(4*18, 26*18,16,16));
    }

    /**
     * Method to find all units that are close enough to the ownerUnit to be considered
     * as potential targets. If it is the case, then their index in the ICWarsArea's unitList
     * is added to the list targetIndexes
     * @return the list of targets' indexes in ICWarsArea's unitList
     */
    private ArrayList<Integer> findTargetsIndexes() {
        ArrayList<Integer> targetsIndexes = new ArrayList<Integer>();
        for (int i = 0; i < area.getUnitListSize(); i++) {
            Unit unit = area.getUnitFromIndex(i);
            if(unit.getFaction() == ownerUnit.getFaction()) continue ;
            DiscreteCoordinates position = new DiscreteCoordinates((int)unit.getPosition().x, (int)unit.getPosition().y);
            if(isInRange(position)) targetsIndexes.add(area.getIndexInUnitList(unit));
        }
        return targetsIndexes ;
    }

    /**
     * Method to check if a given position is close enough to the ownerUnit's position
     * @param position position to check
     */
    private boolean isInRange(DiscreteCoordinates position) {
        if(Math.abs(ownerUnit.getPosition().x - position.x) <= ownerUnit.getRadius() && Math.abs(ownerUnit.getPosition().y - position.y) <= ownerUnit.getRadius()) return true;
        return false;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        waitingPurposeBoolean = true;
        targetsIndexes = findTargetsIndexes();
        if (targetsIndexes.size() == 0 || keyboard.get(Keyboard.TAB).isPressed()) {
            player.centerCamera();
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
        } else {
            targetIndex = targetsIndexes.get(index);
            if (keyboard.get(Keyboard.RIGHT).isPressed() && index < targetsIndexes.size() - 1) {
                ++index;
                targetIndex = targetsIndexes.get(index);
            }
            if (keyboard.get(Keyboard.LEFT).isPressed() && index > 0) {
                --index;
                targetIndex = targetsIndexes.get(index);
            }
            if (keyboard.get(Keyboard.ENTER).isReleased()) {
                area.getUnitFromIndex(targetIndex).takeInjure(ownerUnit.getDamage());
                player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
                waitingPurposeBoolean = false;
            }
            ownerUnit.setHasBeenUsed(true);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (waitingPurposeBoolean) {
            if (targetsIndexes == null) ;
            else {
                area.getUnitFromIndex(targetIndex).centerCamera();
                cursor.setAnchor(canvas.getPosition().add(1, 0));
                cursor.draw(canvas);
            }
        }
    }
}

