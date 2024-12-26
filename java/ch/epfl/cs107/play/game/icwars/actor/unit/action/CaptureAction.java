package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class CaptureAction extends ICWarsAction {

    //-----------------------------------API-------------------------------------//

    public CaptureAction(ICWarsArea area, Unit ownerUnit){
        super(area, ownerUnit, Keyboard.C, "(C)apture");
    }

    /**
     * Method handling a standard capture action, checking if the unit is
     * on an enemy city and then changing its faction to the one of the player
     * (using a setter on a boolean in Unit.java in order to use interactions between
     * a unit and a city).
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        if(ownerUnit.isOnEnemyCity()) {
            ownerUnit.setPlayerHasSelectedCapture(true);
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
        } else {
            player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
        }
    }

    /**
     * Captures city if unit is a tank and is standing on a city :
     * sets city as same faction
     */
    @Override
    public void doAutoAction(float dt, AIPlayer aiPlayer) {
        ownerUnit.setPlayerHasSelectedCapture(true);
        aiPlayer.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
