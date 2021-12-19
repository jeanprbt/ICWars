package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.util.ArrayList;

public class AttackAction extends ICWarsAction{

    private ImageGraphics cursor;
    private Unit target ;
    private ArrayList<Unit> targets;
    private int index;

    // Boolean used to make sure that method draw of AttackAction is called after doAction
    // Without it method draw is called before and error occurs because targetIndexes is not updated soon enough
    private boolean waitingPurposeBoolean;

    //-----------------------------------API-------------------------------------//

    public AttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit, Keyboard.A, "(A)ttack");
        index = 0 ;
        cursor = new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"), 1f, 1f, new RegionOfInterest(4*18, 26*18,16,16));
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        waitingPurposeBoolean = true;
        targets = getCloseEnemies();
        if (targets.size() == 0 || keyboard.get(Keyboard.TAB).isPressed()) {
            player.centerCamera();
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
        } else {
            target = targets.get(index);
            if (keyboard.get(Keyboard.RIGHT).isPressed() && index < targets.size() - 1) {
                ++index;
                target = targets.get(index);
            }
            if (keyboard.get(Keyboard.LEFT).isPressed() && index > 0) {
                --index;
                target = targets.get(index);
            }
            if (keyboard.get(Keyboard.ENTER).isReleased()) {
                target.takeInjure(ownerUnit.getDamage());
                player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
                waitingPurposeBoolean = false;
            }
            ownerUnit.setHasBeenUsed(true);
        }
    }

    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        ArrayList<Unit> targets = getCloseEnemies();
        int minHp = 100;
        Unit targetToAttack = null ;
        for (Unit target : targets) {
            if(target.getHp() < minHp){
                minHp = target.getHp();
                targetToAttack = target ;
            }
        }
        targetToAttack.takeInjure(ownerUnit.getDamage());
        aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
    }


    @Override
    public void draw(Canvas canvas) {
        if (waitingPurposeBoolean) {
            if (target == null) ;
            else {
                target.centerCamera();
                cursor.setAnchor(canvas.getPosition().add(1, 0));
                cursor.draw(canvas);
            }
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    //-----------------------------------Private-------------------------------------//

    /**
     * Method to find all enemies that are close enough to the ownerUnit to be considered
     * as potential targets.
     * @return the list of potential targets
     */
    private ArrayList<Unit> getCloseEnemies() {
        ArrayList<Unit> closeEnemies = area.getEnemies(ownerUnit.getFaction());

        for (Unit enemy : area.getEnemies(ownerUnit.getFaction())) {
            DiscreteCoordinates position = new DiscreteCoordinates((int)enemy.getPosition().x, (int)enemy.getPosition().y);
            if(Math.abs(ownerUnit.getPosition().x - position.x) <= ownerUnit.getRadius() && Math.abs(ownerUnit.getPosition().y - position.y) <= ownerUnit.getRadius()) continue;
            closeEnemies.remove(enemy);
        }

        return closeEnemies ;
    }
}

