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
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;

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
            scope = new RocketManScope(area, new DiscreteCoordinates((int)ownerUnit.getPosition().x-2, (int)ownerUnit.getPosition().y), ownerUnit.getFaction(), RocketMan.getDamageZone());
            area.registerActor(scope);
            scope.resetPosition(ownerUnit.getPosition());
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
            area.unregisterActor(scope);
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
            ownerUnit.setHasBeenUsed(true);
            waitingPurposeBoolean = true;
        }
    }

    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        if(waitingPurposeBoolean){
            area.registerActor(scope);
        }
    }
}
