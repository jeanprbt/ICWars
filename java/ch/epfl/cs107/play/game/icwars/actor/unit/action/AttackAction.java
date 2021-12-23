package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.RPGSprite;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;

public class AttackAction extends ICWarsAction{

    private ImageGraphics cursor;
    private Unit realTarget ;
    Unit aiTarget;
    private ArrayList<Unit> targets;
    private int index;
    private  Animation animation ;
    private Sprite [] sprites ;
    private boolean hasTakenInjure ;
    private boolean isInProgress ;

    //-----------------------------------API-------------------------------------//

    public AttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit, Keyboard.A, "(A)ttack");
        index = 0 ;
        sprites = new Sprite[7];
        sprites[0] = new Sprite("1", 1, 1);
        sprites[1] = new Sprite("2", 1, 1);
        sprites[2] = new Sprite("3", 1, 1);
        sprites[3] = new Sprite("4", 1, 1);
        sprites[4] = new Sprite("5", 1, 1);
        sprites[5] = new Sprite("6", 1, 1);
        sprites[6] = new Sprite("7", 1, 1);
        animation = new Animation(3, sprites, false);
        cursor = new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"), 1f, 1f, new RegionOfInterest(4*18, 26*18,16,16));
        cursor.setDepth(2);
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        targets = getCloseEnemies();
        if (targets.size() == 0 || keyboard.get(Keyboard.TAB).isPressed()) {
            player.centerCamera();
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
        } else  {
            if (!isInProgress) {
                realTarget = targets.get(index);
                    if (keyboard.get(Keyboard.RIGHT).isPressed() && index < targets.size() - 1) {
                      ++index;
                     realTarget = targets.get(index);
                  }
                  if (keyboard.get(Keyboard.LEFT).isPressed() && index > 0) {
                      --index;
                      realTarget = targets.get(index);
                  }
            }
            if (keyboard.get(Keyboard.ENTER).isReleased()) {
                realTarget.takeInjure(ownerUnit.getDamage());
                index = 0 ; 
                hasTakenInjure = true;
                for (int i = 0; i < sprites.length; i++) {
                    sprites[i].setAnchor(realTarget.getPosition());
                    sprites[i].setDepth(2);
                }
            }
            if (animation.isCompleted()) {
                isInProgress = false;
                animation.reset();
                hasTakenInjure = false;
                player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
            }
            ownerUnit.setHasBeenUsed(true);
        }
    }

    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        if (!isInProgress) {
            ArrayList<Unit> targets = getCloseEnemies();
            int minHp = 100;
            aiTarget = null ;
            for (Unit target : targets) {
                if (target.getHp() < minHp) {
                    minHp = target.getHp();
                    aiTarget = target;
                }
            }
        aiTarget.takeInjure(ownerUnit.getDamage());
        hasTakenInjure = true;
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnchor(aiTarget.getPosition());
            sprites[i].setDepth(2);
        }
    }
        if(animation.isCompleted()){
            isInProgress = false ;
            animation.reset();
            hasTakenInjure = false;
            aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        //Draw pour un Real Player
        if (realTarget != null) {
            realTarget.centerCamera();
            cursor.setAnchor(canvas.getPosition().add(1, 0));
            cursor.draw(canvas);
            if (hasTakenInjure && !animation.isCompleted()) {
                isInProgress = true ;
                animation.update(1);
                animation.draw(canvas);
            }
        }
        //Draw pour un AIPlayer
        if (aiTarget != null) {
            aiTarget.centerCamera();
            cursor.setAnchor(aiTarget.getPosition().add(1, 0));
            cursor.draw(canvas);
            if(hasTakenInjure && !animation.isCompleted()) {
                isInProgress = true ;
                animation.update(1);
                animation.draw(canvas);
            }
        }
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

    /**
     * Function making the program wait for a certain time before resuming action
     * @param ms : Time in milliseconds
     */
    private static void waitFor(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

