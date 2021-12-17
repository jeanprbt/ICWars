package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class WaitAction extends ICWarsAction {

    //-----------------------------------API-------------------------------------//

    public WaitAction(ICWarsArea area, Unit ownerUnit){
        super(area, ownerUnit, Keyboard.W, "(W)ait");
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        ownerUnit.setHasBeenUsed(true);
        player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
    }


    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
    }


    @Override
    public void draw(Canvas canvas) {

    }
}
