package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class AttackAction extends ICWarsAction{

    public AttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit, Keyboard.A, "(A)ttack");
    }


    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {

    }

    @Override
    public void draw(Canvas canvas) {

    }

}

