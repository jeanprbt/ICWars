package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.RocketMan;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.scope.RocketManScope;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class RocketManAttackAction extends AttackAction {
    private RocketManScope scope;
    private ArrayList<Unit> targets ;
    private boolean waitingPurposeBoolean = true  ;

    //-----------------------------------API-------------------------------------//

    public RocketManAttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit);

    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        if (waitingPurposeBoolean) {
            scope = new RocketManScope(area, new DiscreteCoordinates((int)area.getWidth() / 2 - 1, (int)area.getHeight() / 2 - 1), ownerUnit.getFaction(), RocketMan.getDamageZone(), true);
            area.registerActor(scope);
            scope.centerCamera();
            waitingPurposeBoolean = false;
        }

            if (scope.hasCollectedTargets()) {
                targets = scope.getTargets();
                    if (targets != null) {
                        for (Unit target : targets) {
                            target.takeInjure(ownerUnit.getDamage());
                        }
                    }
            }
        if (!scope.isInProgress() && scope.hasCollectedTargets()) {
            area.unregisterActor(scope);
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
            ownerUnit.setHasBeenUsed(true);
            waitingPurposeBoolean = true;
        }

    }

    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        if (waitingPurposeBoolean) {
            scope = new RocketManScope(area, new DiscreteCoordinates(area.getWidth() / 2 - 1, area.getHeight() / 2 - 1), ownerUnit.getFaction(), RocketMan.getDamageZone(), false);
            area.registerActor(scope);
            scope.centerCamera();
            waitingPurposeBoolean = false;
        }
        if (!scope.isInProgress()) {
            int minHp = 100;
            aiTarget = null;
            List<Unit> enemies = area.getEnemies(ownerUnit.getFaction());
            for (Unit enemy : enemies) {
                if (enemy.getHp() < minHp) {
                    minHp = enemy.getHp();
                    aiTarget = enemy;
                }
            }
            if(aiTarget.getPosition().x > area.getWidth() - 3 || aiTarget.getPosition().y > area.getHeight() - 3) {
                if (aiTarget.getPosition().x > area.getWidth() - 3) {
                    scope.resetPosition(new Vector(aiTarget.getPosition().x - 2, aiTarget.getPosition().y));
                }
                if (aiTarget.getPosition().y > area.getHeight() - 3) {
                    scope.resetPosition(new Vector(aiTarget.getPosition().x, aiTarget.getPosition().y - 2));
                }
            } else {
                scope.resetPosition(aiTarget.getPosition());
            }

            scope.setAiHasMovedScope(true);
            if (scope.hasCollectedTargets()) {
                List<Unit> targets = scope.getTargets();
                for (Unit target : targets) {
                    target.takeInjure(ownerUnit.getDamage());
                }
            }
                if (scope.animationCompleted()) {
                    area.unregisterActor(scope);
                    aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);

                }
            }
        }
    }
