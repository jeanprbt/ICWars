package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class CaptureAction extends ICWarsAction {

    public CaptureAction(ICWarsArea area, Unit ownerUnit){
        super(area, ownerUnit, Keyboard.C, "(C)apture");
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        if(ownerUnit.isOnEnemyCity()) {
            ownerUnit.setPlayerHasSelectedCapture(true);
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
        } else {
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
        }
    }


    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        ownerUnit.setPlayerHasSelectedCapture(true);
        aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
